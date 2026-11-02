import React, { useState } from 'react';
import { User } from 'firebase/auth';
import {
  ShieldCheck,
  Search,
  Filter,
  CheckCircle2,
  Clock,
  AlertCircle,
  FileText,
  UserCheck,
  ArrowUpDown,
  Send,
  Loader2,
  Eye,
  Edit3
} from 'lucide-react';
import { ServiceApplication, PaymentRecord, ApplicationStatus } from '../types';
import { db, doc, updateDoc, setDoc, serverTimestamp, ADMIN_EMAIL } from '../firebase';

interface AdminDashboardProps {
  user: User;
  applications: ServiceApplication[];
  payments: PaymentRecord[];
  onViewReceipt: (applicationId: string) => void;
}

export const AdminDashboard: React.FC<AdminDashboardProps> = ({
  user,
  applications,
  payments,
  onViewReceipt
}) => {
  const isAdmin = (user.email || '').toLowerCase() === ADMIN_EMAIL.toLowerCase();

  const [filterStatus, setFilterStatus] = useState<string>('All');
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedApp, setSelectedApp] = useState<ServiceApplication | null>(null);

  // Status updating state
  const [newStatus, setNewStatus] = useState<ApplicationStatus>('Under Review');
  const [operatorNotes, setOperatorNotes] = useState('');
  const [isUpdating, setIsUpdating] = useState(false);
  const [successMsg, setSuccessMsg] = useState('');

  if (!isAdmin) {
    return (
      <div className="max-w-xl mx-auto my-20 p-8 bg-red-50 border border-red-200 rounded-3xl text-center space-y-4">
        <ShieldCheck className="w-12 h-12 text-red-600 mx-auto" />
        <h2 className="text-xl font-bold text-red-900">Access Restricted</h2>
        <p className="text-xs text-red-700">
          This administration dashboard is strictly restricted to the authorized owner Google Account (<strong>{ADMIN_EMAIL}</strong>).
        </p>
      </div>
    );
  }

  // Filter logic
  const filteredApps = applications.filter((app) => {
    const matchesStatus = filterStatus === 'All' || app.status === filterStatus;
    const term = searchTerm.toLowerCase();
    const matchesSearch =
      !searchTerm ||
      app.applicationId.toLowerCase().includes(term) ||
      app.customerName.toLowerCase().includes(term) ||
      app.customerPhone.includes(term) ||
      app.serviceName.toLowerCase().includes(term);
    return matchesStatus && matchesSearch;
  });

  const totalRevenue = payments.reduce((acc, p) => acc + (p.status === 'SUCCESS' ? p.amount : 0), 0);
  const pendingCount = applications.filter(a => a.status === 'Pending' || a.status === 'Under Review').length;
  const processingCount = applications.filter(a => a.status === 'Processing').length;
  const completedCount = applications.filter(a => a.status === 'Completed').length;

  const handleUpdateStatus = async () => {
    if (!selectedApp) return;

    setIsUpdating(true);
    setSuccessMsg('');

    try {
      const appRef = doc(db, 'applications', selectedApp.applicationId);
      await updateDoc(appRef, {
        status: newStatus,
        adminNotes: operatorNotes,
        updatedAt: serverTimestamp()
      });

      // Send automated notification to customer
      const notifId = `NOTIF-${Date.now().toString().slice(-8)}`;
      await setDoc(doc(db, 'notifications', notifId), {
        notificationId: notifId,
        userId: selectedApp.userId,
        title: `Application Update: ${selectedApp.serviceName}`,
        message: `Your application (${selectedApp.applicationId}) status changed to: ${newStatus}. Note: ${operatorNotes}`,
        applicationId: selectedApp.applicationId,
        read: false,
        createdAt: serverTimestamp()
      });

      setSuccessMsg(`Status updated to ${newStatus} and customer notified!`);
      setSelectedApp(prev => prev ? { ...prev, status: newStatus, adminNotes: operatorNotes } : null);
    } catch (err: any) {
      alert('Error updating status: ' + err.message);
    } finally {
      setIsUpdating(false);
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 space-y-8">
      
      {/* Header Banner */}
      <div className="bg-slate-900 text-white p-6 sm:p-8 rounded-3xl shadow-xl flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <div className="flex items-center gap-2">
            <span className="bg-amber-400 text-slate-900 text-[10px] font-black px-2.5 py-0.5 rounded uppercase">
              Owner Panel
            </span>
            <span className="text-xs text-slate-400">{user.email}</span>
          </div>
          <h2 className="text-2xl sm:text-3xl font-extrabold mt-1">
            TT Online Services Management Portal
          </h2>
          <p className="text-xs text-slate-300 mt-1">
            Real-time control over customer submissions, document audits, and Razorpay verified revenue.
          </p>
        </div>

        <div className="text-right bg-white/10 px-5 py-3 rounded-2xl border border-white/10">
          <span className="text-[10px] uppercase font-bold text-slate-400 block">Total Live Revenue</span>
          <span className="text-2xl font-black text-emerald-400">₹{totalRevenue.toFixed(0)}</span>
        </div>
      </div>

      {/* Metrics Row */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="p-5 bg-white rounded-2xl border border-slate-200 shadow-sm">
          <span className="text-xs font-semibold text-slate-500 block">All Applications</span>
          <span className="text-2xl font-black text-slate-900">{applications.length}</span>
        </div>
        <div className="p-5 bg-white rounded-2xl border border-slate-200 shadow-sm">
          <span className="text-xs font-semibold text-slate-500 block">Pending Audit</span>
          <span className="text-2xl font-black text-amber-600">{pendingCount}</span>
        </div>
        <div className="p-5 bg-white rounded-2xl border border-slate-200 shadow-sm">
          <span className="text-xs font-semibold text-slate-500 block">Processing at Portal</span>
          <span className="text-2xl font-black text-blue-600">{processingCount}</span>
        </div>
        <div className="p-5 bg-white rounded-2xl border border-slate-200 shadow-sm">
          <span className="text-xs font-semibold text-slate-500 block">Completed / Dispatched</span>
          <span className="text-2xl font-black text-emerald-600">{completedCount}</span>
        </div>
      </div>

      {/* Controls Bar */}
      <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm flex flex-col sm:flex-row justify-between items-center gap-4">
        
        {/* Search */}
        <div className="relative w-full sm:w-80">
          <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="Search by ID, name, mobile, service..."
            className="w-full pl-10 pr-3 py-2 rounded-xl border border-slate-300 text-xs text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        {/* Filter Pills */}
        <div className="flex flex-wrap gap-1.5 w-full sm:w-auto">
          {['All', 'Pending', 'Under Review', 'Processing', 'Completed', 'Rejected'].map((status) => (
            <button
              key={status}
              onClick={() => setFilterStatus(status)}
              className={`px-3 py-1.5 rounded-xl text-xs font-bold transition-colors ${
                filterStatus === status
                  ? 'bg-blue-600 text-white'
                  : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
              }`}
            >
              {status}
            </button>
          ))}
        </div>
      </div>

      {/* Main Applications Table */}
      <div className="bg-white rounded-3xl border border-slate-200 shadow-sm overflow-hidden">
        {filteredApps.length === 0 ? (
          <div className="p-12 text-center text-slate-500 text-xs">
            No applications match the selected criteria.
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 text-slate-700 uppercase font-bold text-[10px] border-b border-slate-200">
                <tr>
                  <th className="p-4">App ID</th>
                  <th className="p-4">Customer</th>
                  <th className="p-4">Service</th>
                  <th className="p-4">Category</th>
                  <th className="p-4">Payment</th>
                  <th className="p-4">Status</th>
                  <th className="p-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {filteredApps.map((app) => (
                  <tr key={app.applicationId} className="hover:bg-slate-50 transition-colors">
                    <td className="p-4 font-mono font-bold text-blue-700">{app.applicationId}</td>
                    <td className="p-4">
                      <span className="font-bold text-slate-900 block">{app.customerName}</span>
                      <span className="text-[11px] text-slate-500">{app.customerPhone}</span>
                    </td>
                    <td className="p-4 font-medium text-slate-800">{app.serviceName}</td>
                    <td className="p-4 text-slate-600">{app.applicationType}</td>
                    <td className="p-4 font-bold text-slate-900">
                      ₹{app.amountPaid}
                      <span className="text-[10px] block text-emerald-600 font-semibold">{app.paymentStatus}</span>
                    </td>
                    <td className="p-4">
                      <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full uppercase ${
                        app.status === 'Completed'
                          ? 'bg-emerald-100 text-emerald-800'
                          : app.status === 'Rejected'
                          ? 'bg-red-100 text-red-800'
                          : 'bg-amber-100 text-amber-800'
                      }`}>
                        {app.status}
                      </span>
                    </td>
                    <td className="p-4 text-right">
                      <div className="flex items-center justify-end gap-1.5">
                        <button
                          onClick={() => {
                            setSelectedApp(app);
                            setNewStatus(app.status);
                            setOperatorNotes(app.adminNotes || '');
                            setSuccessMsg('');
                          }}
                          className="px-2.5 py-1 text-xs font-bold bg-blue-50 text-blue-700 hover:bg-blue-100 rounded-lg flex items-center gap-1"
                        >
                          <Edit3 className="w-3 h-3" />
                          <span>Manage</span>
                        </button>
                        <button
                          onClick={() => onViewReceipt(app.applicationId)}
                          className="px-2.5 py-1 text-xs font-semibold text-slate-600 hover:bg-slate-100 rounded-lg"
                        >
                          Receipt
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Operator Modal for Selected Application */}
      {selectedApp && (
        <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl max-w-2xl w-full p-6 space-y-6 shadow-2xl border border-slate-200 animate-in fade-in zoom-in-95 duration-200">
            
            <div className="flex justify-between items-start border-b border-slate-200 pb-4">
              <div>
                <span className="font-mono text-xs font-bold text-blue-700 bg-blue-50 px-2 py-0.5 rounded">
                  {selectedApp.applicationId}
                </span>
                <h3 className="text-xl font-extrabold text-slate-900 mt-1">
                  Manage: {selectedApp.serviceName}
                </h3>
                <p className="text-xs text-slate-500">
                  Applicant: <strong>{selectedApp.customerName}</strong> ({selectedApp.customerEmail} | {selectedApp.customerPhone})
                </p>
              </div>
              <button
                onClick={() => setSelectedApp(null)}
                className="text-slate-400 hover:text-slate-700 p-1 rounded-full"
              >
                ✕
              </button>
            </div>

            {successMsg && (
              <div className="p-3 bg-emerald-50 border border-emerald-200 text-emerald-800 rounded-xl text-xs font-bold flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 text-emerald-600" />
                <span>{successMsg}</span>
              </div>
            )}

            {/* Application Data Snapshot */}
            <div className="bg-slate-50 p-4 rounded-2xl border border-slate-200 space-y-2 text-xs">
              <div className="grid grid-cols-2 gap-2">
                <div>
                  <span className="text-slate-500 font-semibold block text-[10px] uppercase">Service Type:</span>
                  <span className="font-bold text-slate-800">{selectedApp.applicationType}</span>
                </div>
                <div>
                  <span className="text-slate-500 font-semibold block text-[10px] uppercase">Amount Collected:</span>
                  <span className="font-bold text-slate-800">₹{selectedApp.amountPaid} ({selectedApp.paymentStatus})</span>
                </div>
              </div>
            </div>

            {/* Status Change Form */}
            <div className="space-y-4">
              <div>
                <label className="text-xs font-bold text-slate-700 uppercase tracking-wide block mb-1">
                  Update Application Status
                </label>
                <select
                  value={newStatus}
                  onChange={(e) => setNewStatus(e.target.value as ApplicationStatus)}
                  className="w-full px-3.5 py-2.5 rounded-xl border border-slate-300 text-sm font-bold text-slate-800 focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
                >
                  <option value="Pending">Pending (Awaiting Audit)</option>
                  <option value="Under Review">Under Review (Operator Verifying Docs)</option>
                  <option value="Processing">Processing (Filing on Govt Portal)</option>
                  <option value="Completed">Completed (Certificate Issued / Dispatched)</option>
                  <option value="Rejected">Rejected (Deficient Documents)</option>
                  <option value="Additional Information Required">Additional Information Required</option>
                </select>
              </div>

              <div>
                <label className="text-xs font-bold text-slate-700 uppercase tracking-wide block mb-1">
                  Operator Notes / Govt Ack Ref Number
                </label>
                <textarea
                  value={operatorNotes}
                  onChange={(e) => setOperatorNotes(e.target.value)}
                  placeholder="e.g. Filed on NSDL portal with Ack No 88102938472. Physical card dispatched via India Post Speed Post tracker EB123456789IN."
                  rows={3}
                  className="w-full px-3.5 py-2 rounded-xl border border-slate-300 text-xs text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500"
                ></textarea>
              </div>
            </div>

            {/* Modal Actions */}
            <div className="flex justify-between items-center pt-4 border-t border-slate-200">
              <button
                onClick={() => setSelectedApp(null)}
                className="px-4 py-2 text-xs font-semibold text-slate-600 hover:bg-slate-100 rounded-xl"
              >
                Close
              </button>
              <button
                onClick={handleUpdateStatus}
                disabled={isUpdating}
                className="flex items-center gap-1.5 px-6 py-2.5 bg-blue-600 hover:bg-blue-700 text-white text-xs font-bold rounded-xl shadow-md active:scale-95 transition-all"
              >
                {isUpdating ? <Loader2 className="w-3.5 h-3.5 animate-spin" /> : <Send className="w-3.5 h-3.5" />}
                <span>Save & Notify Customer</span>
              </button>
            </div>

          </div>
        </div>
      )}

    </div>
  );
};
