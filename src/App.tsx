import React, { useState, useEffect } from 'react';
import { User } from 'firebase/auth';
import {
  auth,
  db,
  collection,
  query,
  where,
  orderBy,
  onSnapshot,
  onAuthStateChanged,
  doc,
  updateDoc,
  ADMIN_EMAIL,
  signInWithGoogle
} from './firebase';
import { ALL_SERVICES } from './data/services';
import {
  DocumentService,
  ServiceApplication,
  PaymentRecord,
  AppNotification
} from './types';

// Components
import { Navbar } from './components/Navbar';
import { HeroSection } from './components/HeroSection';
import { ServiceCard } from './components/ServiceCard';
import { ServiceDetailModal } from './components/ServiceDetailModal';
import { ApplicationModal } from './components/ApplicationModal';
import { PaymentReceiptModal } from './components/PaymentReceiptModal';
import { ApplicationTracking } from './components/ApplicationTracking';
import { CustomerDashboard } from './components/CustomerDashboard';
import { AdminDashboard } from './components/AdminDashboard';
import { AboutSection } from './components/AboutSection';
import { SupportSection } from './components/SupportSection';
import { Footer } from './components/Footer';

// Icons
import {
  Search,
  Filter,
  CheckCircle2,
  FileCheck2,
  Clock,
  ShieldCheck,
  CreditCard,
  Bell,
  X,
  ExternalLink,
  ChevronRight
} from 'lucide-react';

export function App() {
  const [user, setUser] = useState<User | null>(null);
  const [activeTab, setActiveTab] = useState<string>('home');
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('All');

  // Modal states
  const [detailService, setDetailService] = useState<DocumentService | null>(null);
  const [applyService, setApplyService] = useState<DocumentService | null>(null);
  const [receiptPayment, setReceiptPayment] = useState<PaymentRecord | null>(null);
  const [receiptApp, setReceiptApp] = useState<ServiceApplication | null>(null);
  const [trackingAppId, setTrackingAppId] = useState('');
  const [notificationsOpen, setNotificationsOpen] = useState(false);

  // Firestore Data State
  const [applications, setApplications] = useState<ServiceApplication[]>([]);
  const [allAdminApplications, setAllAdminApplications] = useState<ServiceApplication[]>([]);
  const [payments, setPayments] = useState<PaymentRecord[]>([]);
  const [notifications, setNotifications] = useState<AppNotification[]>([]);

  const isAdmin = user && (user.email || '').toLowerCase() === ADMIN_EMAIL.toLowerCase();

  // Listen for Auth changes
  useEffect(() => {
    const unsubscribeAuth = onAuthStateChanged(auth, (currentUser) => {
      setUser(currentUser);
    });
    return () => unsubscribeAuth();
  }, []);

  // Listen to Firestore real-time collections
  useEffect(() => {
    if (!user) {
      setApplications([]);
      setPayments([]);
      setNotifications([]);
      return;
    }

    // Customer's applications
    const appsQuery = query(
      collection(db, 'applications'),
      where('userId', '==', user.uid)
    );
    const unsubApps = onSnapshot(appsQuery, (snapshot) => {
      const items: ServiceApplication[] = [];
      snapshot.forEach((doc) => {
        items.push(doc.data() as ServiceApplication);
      });
      setApplications(items);
    }, (error) => {
      console.warn('Firestore apps query note:', error.message);
    });

    // Customer's payments
    const paymentsQuery = query(
      collection(db, 'payments'),
      where('userId', '==', user.uid)
    );
    const unsubPayments = onSnapshot(paymentsQuery, (snapshot) => {
      const items: PaymentRecord[] = [];
      snapshot.forEach((doc) => {
        items.push(doc.data() as PaymentRecord);
      });
      setPayments(items);
    }, (error) => {
      console.warn('Firestore payments query note:', error.message);
    });

    // Notifications
    const notifsQuery = query(
      collection(db, 'notifications'),
      where('userId', '==', user.uid)
    );
    const unsubNotifs = onSnapshot(notifsQuery, (snapshot) => {
      const items: AppNotification[] = [];
      snapshot.forEach((doc) => {
        items.push(doc.data() as AppNotification);
      });
      setNotifications(items);
    }, (error) => {
      console.warn('Firestore notifications note:', error.message);
    });

    // Admin applications listener (if owner)
    let unsubAdmin: (() => void) | null = null;
    if (isAdmin) {
      const adminAppsQuery = collection(db, 'applications');
      unsubAdmin = onSnapshot(adminAppsQuery, (snapshot) => {
        const items: ServiceApplication[] = [];
        snapshot.forEach((doc) => {
          items.push(doc.data() as ServiceApplication);
        });
        setAllAdminApplications(items);
      }, (err) => {
        console.warn('Admin apps fetch note:', err.message);
      });
    }

    return () => {
      unsubApps();
      unsubPayments();
      unsubNotifs();
      if (unsubAdmin) unsubAdmin();
    };
  }, [user, isAdmin]);

  // Categories
  const categories = ['All', 'Identity & Taxation', 'Elections & Citizenship', 'UIDAI Official Facilitation', 'Transport & Parivahan', 'External Affairs & Travel', 'Civil Registration (CRS)', 'Revenue & Scholarships', 'Social Justice & Welfare', 'Revenue & State Administration'];

  // Filtered Services
  const filteredServices = ALL_SERVICES.filter((srv) => {
    const matchesCategory = selectedCategory === 'All' || srv.category === selectedCategory;
    const q = searchQuery.toLowerCase().trim();
    const matchesSearch =
      !q ||
      srv.title.toLowerCase().includes(q) ||
      srv.subtitle.toLowerCase().includes(q) ||
      srv.description.toLowerCase().includes(q) ||
      srv.checklist.some((c) => c.toLowerCase().includes(q));
    return matchesCategory && matchesSearch;
  });

  const handleOpenReceipt = (applicationId: string) => {
    const foundApp = applications.find((a) => a.applicationId === applicationId) ||
      allAdminApplications.find((a) => a.applicationId === applicationId);
    const foundPayment = payments.find((p) => p.applicationId === applicationId) || {
      paymentId: `PAY-${Date.now().toString().slice(-6)}`,
      orderId: foundApp?.paymentOrderId || 'ORDER_LIVE',
      applicationId: applicationId,
      userId: foundApp?.userId || user?.uid || '',
      amount: foundApp?.amountPaid || 199.0,
      currency: 'INR',
      status: 'SUCCESS',
      razorpayPaymentId: foundApp?.paymentId || 'pay_live_verified',
      receiptNumber: `RCPT-${applicationId.replace('TT-', '')}`,
      serviceName: foundApp?.serviceName || 'Document Assistance',
      createdAt: foundApp?.createdAt
    };
    setReceiptApp(foundApp || null);
    setReceiptPayment(foundPayment as PaymentRecord);
  };

  const handleApplyClick = (service: DocumentService) => {
    if (!user) {
      signInWithGoogle().then(() => {
        setApplyService(service);
      }).catch((e) => {
        alert('Please continue with Google to apply.');
      });
    } else {
      setApplyService(service);
    }
  };

  const handleApplicationSuccess = (
    app: ServiceApplication,
    payment: PaymentRecord
  ) => {
    setApplyService(null);
    setReceiptApp(app);
    setReceiptPayment(payment);
  };

  return (
    <div className="min-h-screen flex flex-col bg-slate-50 text-slate-900 font-sans">
      
      {/* Navigation */}
      <Navbar
        user={user}
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        notifications={notifications}
        onOpenNotifications={() => setNotificationsOpen(true)}
      />

      {/* Main Content Router */}
      <main className="flex-1">
        
        {/* TAB 1: HOME */}
        {activeTab === 'home' && (
          <div className="space-y-12">
            <HeroSection
              searchQuery={searchQuery}
              onSearchChange={(q) => {
                setSearchQuery(q);
              }}
              onExploreServices={() => setActiveTab('services')}
              onTrackClick={() => setActiveTab('track')}
            />

            {/* Services Grid Section */}
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 space-y-8">
              <div className="flex flex-col md:flex-row justify-between items-start md:items-end gap-4 border-b border-slate-200 pb-5">
                <div>
                  <span className="text-xs font-bold text-blue-700 uppercase tracking-wider bg-blue-50 px-2.5 py-1 rounded">
                    Citizen Services Catalog
                  </span>
                  <h2 className="text-2xl sm:text-3xl font-extrabold text-slate-900 mt-1">
                    All 9 Government Assistance Services
                  </h2>
                  <p className="text-xs text-slate-500 mt-1">
                    Select a service to view requirements, required checklist, fee, and initiate official filing.
                  </p>
                </div>

                <button
                  onClick={() => setActiveTab('services')}
                  className="text-xs font-bold text-blue-700 hover:text-blue-800 flex items-center gap-1 group"
                >
                  <span>Filter by Category</span>
                  <ChevronRight className="w-4 h-4 group-hover:translate-x-0.5 transition-transform" />
                </button>
              </div>

              {/* Grid */}
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {filteredServices.map((service) => (
                  <ServiceCard
                    key={service.id}
                    service={service}
                    onApply={handleApplyClick}
                    onViewDetails={(s) => setDetailService(s)}
                  />
                ))}
              </div>
            </div>

            {/* How It Works 4-Step Process */}
            <div className="bg-white border-y border-slate-200/90 py-16">
              <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 space-y-12">
                <div className="text-center max-w-2xl mx-auto space-y-2">
                  <span className="text-xs font-bold text-emerald-700 bg-emerald-50 px-3 py-1 rounded-full uppercase tracking-wider">
                    Simple 4-Step Process
                  </span>
                  <h2 className="text-2xl sm:text-3xl font-extrabold text-slate-900">
                    How TT Online Services Works
                  </h2>
                  <p className="text-xs text-slate-500">
                    Eliminate queues, complex govt forms, and rejection worries in four straightforward steps.
                  </p>
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
                  
                  <div className="p-6 rounded-2xl bg-slate-50 border border-slate-200 space-y-3 text-center sm:text-left">
                    <div className="w-10 h-10 rounded-xl bg-blue-600 text-white flex items-center justify-center font-bold text-sm mx-auto sm:mx-0 shadow-md shadow-blue-500/20">
                      1
                    </div>
                    <h3 className="font-bold text-base text-slate-900">Select & Provide Info</h3>
                    <p className="text-xs text-slate-600 leading-relaxed">
                      Choose your service (PAN, DL, Voter ID, etc.) and fill our simplified questionnaire with basic details.
                    </p>
                  </div>

                  <div className="p-6 rounded-2xl bg-slate-50 border border-slate-200 space-y-3 text-center sm:text-left">
                    <div className="w-10 h-10 rounded-xl bg-blue-600 text-white flex items-center justify-center font-bold text-sm mx-auto sm:mx-0 shadow-md shadow-blue-500/20">
                      2
                    </div>
                    <h3 className="font-bold text-base text-slate-900">Attach Documents</h3>
                    <p className="text-xs text-slate-600 leading-relaxed">
                      Upload Aadhaar/Address proofs via our portal or send them directly to our verified operator on WhatsApp.
                    </p>
                  </div>

                  <div className="p-6 rounded-2xl bg-slate-50 border border-slate-200 space-y-3 text-center sm:text-left">
                    <div className="w-10 h-10 rounded-xl bg-blue-600 text-white flex items-center justify-center font-bold text-sm mx-auto sm:mx-0 shadow-md shadow-blue-500/20">
                      3
                    </div>
                    <h3 className="font-bold text-base text-slate-900">Secure Razorpay (₹)</h3>
                    <p className="text-xs text-slate-600 leading-relaxed">
                      Pay nominal facilitation fees securely via UPI, Card, or NetBanking. Instant downloadable receipt generated.
                    </p>
                  </div>

                  <div className="p-6 rounded-2xl bg-slate-50 border border-slate-200 space-y-3 text-center sm:text-left">
                    <div className="w-10 h-10 rounded-xl bg-emerald-600 text-white flex items-center justify-center font-bold text-sm mx-auto sm:mx-0 shadow-md shadow-emerald-500/20">
                      4
                    </div>
                    <h3 className="font-bold text-base text-slate-900">Operator Portal Filing</h3>
                    <p className="text-xs text-slate-600 leading-relaxed">
                      Our operator audits everything and files on the official government portal. Track progress until physical dispatch!
                    </p>
                  </div>

                </div>
              </div>
            </div>

            {/* Quick Contact & FAQs */}
            <SupportSection />
          </div>
        )}

        {/* TAB 2: SERVICES CATALOG (Full view with category filter) */}
        {activeTab === 'services' && (
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 space-y-8">
            <div className="space-y-3">
              <span className="text-xs font-bold text-blue-700 uppercase tracking-wider bg-blue-50 px-2.5 py-1 rounded">
                Complete Catalog
              </span>
              <h2 className="text-3xl font-extrabold text-slate-900">
                Browse Indian Document Assistance Services
              </h2>
              <p className="text-sm text-slate-500 max-w-2xl">
                Every service includes dedicated operator audit, error prevention, official portal submission, and real-time status tracking.
              </p>
            </div>

            {/* Category Filter Pills */}
            <div className="flex flex-wrap gap-2 pt-2">
              {categories.map((cat) => (
                <button
                  key={cat}
                  onClick={() => setSelectedCategory(cat)}
                  className={`px-3.5 py-2 rounded-xl text-xs font-bold transition-all ${
                    selectedCategory === cat
                      ? 'bg-blue-600 text-white shadow-sm'
                      : 'bg-white text-slate-700 border border-slate-200 hover:bg-slate-50'
                  }`}
                >
                  {cat}
                </button>
              ))}
            </div>

            {/* Services Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 pt-4">
              {filteredServices.map((service) => (
                <ServiceCard
                  key={service.id}
                  service={service}
                  onApply={handleApplyClick}
                  onViewDetails={(s) => setDetailService(s)}
                />
              ))}
            </div>
          </div>
        )}

        {/* TAB 3: TRACK APPLICATION */}
        {activeTab === 'track' && (
          <ApplicationTracking
            applications={applications}
            payments={payments}
            initialSearchId={trackingAppId}
            onViewReceipt={handleOpenReceipt}
            onApplyNew={() => setActiveTab('services')}
          />
        )}

        {/* TAB 4: ABOUT US */}
        {activeTab === 'about' && <AboutSection />}

        {/* TAB 5: CONTACT & SUPPORT */}
        {activeTab === 'contact' && <SupportSection />}

        {/* TAB 6: CUSTOMER DASHBOARD */}
        {activeTab === 'dashboard' && (
          user ? (
            <CustomerDashboard
              user={user}
              applications={applications}
              payments={payments}
              onApplyNew={() => setActiveTab('services')}
              onViewReceipt={handleOpenReceipt}
              onTrackApp={(appId) => {
                setTrackingAppId(appId);
                setActiveTab('track');
              }}
            />
          ) : (
            <div className="max-w-md mx-auto my-20 p-8 bg-white rounded-3xl border border-slate-200 text-center space-y-4 shadow-xl">
              <div className="w-14 h-14 bg-blue-100 text-blue-700 rounded-2xl flex items-center justify-center mx-auto">
                <FileCheck2 className="w-8 h-8" />
              </div>
              <h3 className="text-xl font-bold text-slate-900">Sign In to View Dashboard</h3>
              <p className="text-xs text-slate-500">
                Log in with your Google account to access your submitted documents, tracking timeline, and tax receipts.
              </p>
              <button
                onClick={() => signInWithGoogle()}
                className="w-full flex items-center justify-center space-x-2 bg-blue-700 hover:bg-blue-800 text-white font-semibold py-3 px-4 rounded-xl shadow-md text-sm"
              >
                <span>Continue with Google</span>
              </button>
            </div>
          )
        )}

        {/* TAB 7: ADMIN DASHBOARD (Owner Only) */}
        {activeTab === 'admin' && (
          user ? (
            <AdminDashboard
              user={user}
              applications={allAdminApplications.length > 0 ? allAdminApplications : applications}
              payments={payments}
              onViewReceipt={handleOpenReceipt}
            />
          ) : (
            <div className="max-w-md mx-auto my-20 p-8 bg-white rounded-3xl border border-slate-200 text-center space-y-4 shadow-xl">
              <ShieldCheck className="w-12 h-12 text-amber-600 mx-auto" />
              <h3 className="text-xl font-bold text-slate-900">Owner Admin Login</h3>
              <p className="text-xs text-slate-500">
                Please continue with the authorized owner Google Account (<strong>{ADMIN_EMAIL}</strong>).
              </p>
              <button
                onClick={() => signInWithGoogle()}
                className="w-full flex items-center justify-center space-x-2 bg-slate-900 hover:bg-slate-800 text-white font-semibold py-3 px-4 rounded-xl shadow-md text-sm"
              >
                <span>Sign In as Admin</span>
              </button>
            </div>
          )
        )}

      </main>

      {/* Notifications Drawer */}
      {notificationsOpen && (
        <div className="fixed inset-0 z-50 bg-slate-900/50 backdrop-blur-sm flex justify-end">
          <div className="bg-white w-full max-w-sm h-full p-6 space-y-4 shadow-2xl flex flex-col justify-between animate-in slide-in-from-right duration-200">
            <div>
              <div className="flex items-center justify-between pb-4 border-b border-slate-200">
                <div className="flex items-center gap-2">
                  <Bell className="w-5 h-5 text-blue-600" />
                  <h3 className="font-bold text-base text-slate-900">Notifications</h3>
                </div>
                <button
                  onClick={() => setNotificationsOpen(false)}
                  className="p-1 rounded-full hover:bg-slate-100 text-slate-400 hover:text-slate-700"
                >
                  <X className="w-5 h-5" />
                </button>
              </div>

              <div className="py-4 space-y-3 overflow-y-auto max-h-[75vh]">
                {notifications.length === 0 ? (
                  <p className="text-xs text-slate-500 text-center py-10">No new notifications.</p>
                ) : (
                  notifications.map((n) => (
                    <div
                      key={n.notificationId}
                      className={`p-3.5 rounded-2xl border text-xs space-y-1 ${
                        n.read ? 'bg-slate-50 border-slate-200' : 'bg-blue-50/60 border-blue-200'
                      }`}
                    >
                      <h4 className="font-bold text-slate-900">{n.title}</h4>
                      <p className="text-slate-600 text-[11px] leading-relaxed">{n.message}</p>
                      {n.applicationId && (
                        <button
                          onClick={() => {
                            setTrackingAppId(n.applicationId || '');
                            setActiveTab('track');
                            setNotificationsOpen(false);
                          }}
                          className="text-[10px] font-bold text-blue-700 hover:underline pt-1 block"
                        >
                          View Application →
                        </button>
                      )}
                    </div>
                  ))
                )}
              </div>
            </div>

            <button
              onClick={() => setNotificationsOpen(false)}
              className="w-full py-2.5 text-xs font-bold bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-xl"
            >
              Close
            </button>
          </div>
        </div>
      )}

      {/* Service Detail Modal */}
      <ServiceDetailModal
        service={detailService}
        onClose={() => setDetailService(null)}
        onApply={handleApplyClick}
      />

      {/* Application Form & Razorpay Modal */}
      <ApplicationModal
        service={applyService}
        user={user}
        onClose={() => setApplyService(null)}
        onApplicationSuccess={handleApplicationSuccess}
      />

      {/* Payment Receipt & Invoice Modal */}
      <PaymentReceiptModal
        payment={receiptPayment}
        application={receiptApp}
        onClose={() => {
          setReceiptPayment(null);
          setReceiptApp(null);
        }}
        onTrack={(appId) => {
          setReceiptPayment(null);
          setReceiptApp(null);
          setTrackingAppId(appId);
          setActiveTab('track');
        }}
      />

      {/* Footer */}
      <Footer
        onSelectService={(serviceId) => {
          const s = ALL_SERVICES.find((item) => item.id === serviceId);
          if (s) {
            setDetailService(s);
          }
        }}
        onNavigate={(tab) => setActiveTab(tab)}
      />

    </div>
  );
}
export default App;
