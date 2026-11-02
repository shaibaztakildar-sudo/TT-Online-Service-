import React, { useState } from 'react';
import { User } from 'firebase/auth';
import {
  FileText,
  Clock,
  CheckCircle2,
  Receipt,
  PlusCircle,
  ExternalLink,
  ChevronRight,
  CreditCard,
  AlertCircle
} from 'lucide-react';
import { ServiceApplication, PaymentRecord } from '../types';

interface CustomerDashboardProps {
  user: User;
  applications: ServiceApplication[];
  payments: PaymentRecord[];
  onApplyNew: () => void;
  onViewReceipt: (applicationId: string) => void;
  onTrackApp: (applicationId: string) => void;
}

export const CustomerDashboard: React.FC<CustomerDashboardProps> = ({
  user,
  applications,
  payments,
  onApplyNew,
  onViewReceipt,
  onTrackApp
}) => {
  const [activeTab, setActiveTab] = useState<'applications' | 'payments'>('applications');

  const pendingCount = applications.filter(a => a.status === 'Pending' || a.status === 'Under Review').length;
  const inProgressCount = applications.filter(a => a.status === 'Processing').length;
  const completedCount = applications.filter(a => a.status === 'Completed').length;
  const totalPaidInr = payments.reduce((acc, p) => acc + (p.status === 'SUCCESS' ? p.amount : 0), 0);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 space-y-8">
      
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-blue-900 via-indigo-900 to-slate-900 text-white p-6 sm:p-8 rounded-3xl shadow-xl flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <span className="text-xs font-bold text-blue-300 uppercase tracking-wider">
            Customer Dashboard
          </span>
          <h2 className="text-2xl sm:text-3xl font-extrabold mt-1">
            Welcome, {user.displayName || 'Customer'}
          </h2>
          <p className="text-xs text-slate-300 mt-1">
            Logged in via Google ({user.email}). View your active applications, documents, and payment receipts.
          </p>
        </div>

        <button
          onClick={onApplyNew}
          className="flex items-center gap-2 bg-blue-600 hover:bg-blue-500 text-white font-bold text-sm px-5 py-3 rounded-2xl shadow-lg shadow-blue-600/30 active:scale-95 transition-all"
        >
          <PlusCircle className="w-4 h-4" />
          <span>New Application</span>
        </button>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="p-5 bg-white rounded-2xl border border-slate-200 shadow-sm">
          <span className="text-xs font-semibold text-slate-500 block">Total Submissions</span>
          <span className="text-2xl font-black text-slate-900">{applications.length}</span>
        </div>
        <div className="p-5 bg-white rounded-2xl border border-slate-200 shadow-sm">
          <span className="text-xs font-semibold text-slate-500 block">Pending / Under Review</span>
          <span className="text-2xl font-black text-amber-600">{pendingCount}</span>
        </div>
        <div className="p-5 bg-white rounded-2xl border border-slate-200 shadow-sm">
          <span className="text-xs font-semibold text-slate-500 block">Completed</span>
          <span className="text-2xl font-black text-emerald-600">{completedCount}</span>
        </div>
        <div className="p-5 bg-white rounded-2xl border border-slate-200 shadow-sm">
          <span className="text-xs font-semibold text-slate-500 block">Total Paid (₹)</span>
          <span className="text-2xl font-black text-blue-700">₹{totalPaidInr.toFixed(0)}</span>
        </div>
      </div>

      {/* Toggle Tabs */}
      <div className="flex border-b border-slate-200 gap-4">
        <button
          onClick={() => setActiveTab('applications')}
          className={`pb-3 font-bold text-sm border-b-2 transition-all ${
            activeTab === 'applications'
              ? 'text-blue-700 border-blue-700'
              : 'text-slate-500 border-transparent hover:text-slate-800'
          }`}
        >
          My Applications ({applications.length})
        </button>
        <button
          onClick={() => setActiveTab('payments')}
          className={`pb-3 font-bold text-sm border-b-2 transition-all ${
            activeTab === 'payments'
              ? 'text-blue-700 border-blue-700'
              : 'text-slate-500 border-transparent hover:text-slate-800'
          }`}
        >
          Payment History ({payments.length})
        </button>
      </div>

      {/* Tab: Applications */}
      {activeTab === 'applications' && (
        <div className="space-y-4">
          {applications.length === 0 ? (
            <div className="text-center py-16 bg-white rounded-3xl border border-slate-200 p-8">
              <FileText className="w-12 h-12 text-slate-300 mx-auto mb-3" />
              <h4 className="text-base font-bold text-slate-800">No Applications Yet</h4>
              <p className="text-xs text-slate-500 max-w-sm mx-auto mt-1 mb-5">
                Apply for PAN, Voter ID, Aadhaar appointment, Driving Licence, or Passport assistance to start.
              </p>
              <button
                onClick={onApplyNew}
                className="px-5 py-2.5 bg-blue-600 hover:bg-blue-700 text-white text-xs font-bold rounded-xl"
              >
                Apply for a Service
              </button>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {applications.map((app) => (
                <div
                  key={app.applicationId}
                  className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm hover:border-blue-400 transition-all flex flex-col justify-between space-y-4"
                >
                  <div>
                    <div className="flex items-center justify-between gap-2 mb-2">
                      <span className="font-mono text-xs font-bold text-blue-700 bg-blue-50 px-2 py-0.5 rounded">
                        {app.applicationId}
                      </span>
                      <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full uppercase ${
                        app.status === 'Completed'
                          ? 'bg-emerald-100 text-emerald-800'
                          : app.status === 'Rejected'
                          ? 'bg-red-100 text-red-800'
                          : 'bg-amber-100 text-amber-800'
                      }`}>
                        {app.status}
                      </span>
                    </div>

                    <h3 className="font-bold text-base text-slate-900">{app.serviceName}</h3>
                    <p className="text-xs text-slate-500">Category: {app.applicationType}</p>
                    <p className="text-xs text-slate-500 mt-1">Applicant: <strong>{app.customerName}</strong></p>
                  </div>

                  <div className="p-3 bg-slate-50 rounded-xl text-xs text-slate-600">
                    <span className="font-bold text-slate-700 block text-[10px] uppercase">Status Update:</span>
                    <p className="text-[11px] mt-0.5 text-slate-600 truncate">{app.adminNotes || 'Queued for operator review.'}</p>
                  </div>

                  <div className="pt-2 border-t border-slate-100 flex items-center justify-between">
                    <span className="font-extrabold text-sm text-slate-900">₹{app.amountPaid}</span>
                    <div className="flex items-center gap-2">
                      <button
                        onClick={() => onViewReceipt(app.applicationId)}
                        className="px-3 py-1.5 text-xs font-semibold text-slate-700 hover:text-blue-700 bg-slate-100 hover:bg-slate-200 rounded-lg flex items-center gap-1"
                      >
                        <Receipt className="w-3 h-3 text-blue-600" />
                        <span>Receipt</span>
                      </button>
                      <button
                        onClick={() => onTrackApp(app.applicationId)}
                        className="px-3 py-1.5 text-xs font-bold text-white bg-blue-600 hover:bg-blue-700 rounded-lg flex items-center gap-1"
                      >
                        <span>Track</span>
                        <ChevronRight className="w-3 h-3" />
                      </button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Tab: Payments */}
      {activeTab === 'payments' && (
        <div className="bg-white rounded-3xl border border-slate-200 shadow-sm overflow-hidden">
          {payments.length === 0 ? (
            <div className="p-12 text-center text-slate-500 text-xs">
              No payment transactions recorded yet.
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs">
                <thead className="bg-slate-50 text-slate-700 uppercase font-bold text-[10px] border-b border-slate-200">
                  <tr>
                    <th className="p-4">Receipt No</th>
                    <th className="p-4">Service</th>
                    <th className="p-4">App Ref</th>
                    <th className="p-4">Razorpay Payment ID</th>
                    <th className="p-4">Amount</th>
                    <th className="p-4">Status</th>
                    <th className="p-4 text-right">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {payments.map((p) => (
                    <tr key={p.paymentId} className="hover:bg-slate-50">
                      <td className="p-4 font-mono font-bold text-slate-900">{p.receiptNumber}</td>
                      <td className="p-4 font-medium text-slate-800">{p.serviceName}</td>
                      <td className="p-4 font-mono text-blue-600">{p.applicationId}</td>
                      <td className="p-4 font-mono text-slate-500">{p.razorpayPaymentId}</td>
                      <td className="p-4 font-extrabold text-slate-900">₹{p.amount}</td>
                      <td className="p-4">
                        <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-emerald-100 text-emerald-800">
                          {p.status}
                        </span>
                      </td>
                      <td className="p-4 text-right">
                        <button
                          onClick={() => onViewReceipt(p.applicationId)}
                          className="px-2.5 py-1 text-xs font-bold text-blue-600 hover:bg-blue-50 rounded"
                        >
                          Invoice
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

    </div>
  );
};
