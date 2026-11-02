import React, { useState } from 'react';
import {
  Search,
  CheckCircle2,
  Clock,
  AlertCircle,
  FileText,
  Receipt,
  MessageCircle,
  ChevronRight,
  ShieldAlert
} from 'lucide-react';
import { ServiceApplication, PaymentRecord } from '../types';

interface ApplicationTrackingProps {
  applications: ServiceApplication[];
  payments: PaymentRecord[];
  initialSearchId?: string;
  onViewReceipt: (applicationId: string) => void;
  onApplyNew: () => void;
}

export const ApplicationTracking: React.FC<ApplicationTrackingProps> = ({
  applications,
  payments,
  initialSearchId = '',
  onViewReceipt,
  onApplyNew
}) => {
  const [searchId, setSearchId] = useState(initialSearchId);
  const [searchedApp, setSearchedApp] = useState<ServiceApplication | null>(
    initialSearchId
      ? applications.find(a => a.applicationId.toLowerCase() === initialSearchId.toLowerCase()) || null
      : null
  );

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    const query = searchId.trim().toLowerCase();
    if (!query) return;

    const found = applications.find(
      a => a.applicationId.toLowerCase() === query ||
           a.customerPhone.includes(query) ||
           a.customerEmail.toLowerCase() === query
    );
    setSearchedApp(found || null);
  };

  const getStepState = (currentStatus: string, stepIndex: number) => {
    const statuses = ['Pending', 'Under Review', 'Processing', 'Completed'];
    const currentIndex = statuses.indexOf(currentStatus);
    
    if (currentStatus === 'Rejected') {
      return stepIndex === 0 ? 'completed' : 'rejected';
    }

    if (currentIndex >= stepIndex) return 'completed';
    if (currentIndex === stepIndex - 1) return 'current';
    return 'upcoming';
  };

  return (
    <div className="max-w-5xl mx-auto px-4 py-12 space-y-8">
      
      {/* Header */}
      <div className="text-center max-w-2xl mx-auto space-y-2">
        <span className="text-xs font-bold text-blue-600 bg-blue-50 px-3 py-1 rounded-full uppercase tracking-wider">
          Real-Time Verification
        </span>
        <h2 className="text-3xl font-extrabold text-slate-900">
          Track Your Application Status
        </h2>
        <p className="text-sm text-slate-500">
          Enter your Application ID (e.g. <span className="font-mono font-semibold text-slate-700">TT-123456-7890</span>) or registered phone number to view live progress.
        </p>
      </div>

      {/* Search Bar */}
      <form onSubmit={handleSearch} className="max-w-xl mx-auto flex gap-2">
        <div className="relative flex-1">
          <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
          <input
            type="text"
            value={searchId}
            onChange={(e) => setSearchId(e.target.value)}
            placeholder="Enter Application ID (TT-...) or Mobile..."
            className="w-full pl-12 pr-4 py-3.5 rounded-2xl border border-slate-300 bg-white text-sm font-semibold text-slate-900 focus:outline-none focus:ring-2 focus:ring-blue-500 shadow-sm"
          />
        </div>
        <button
          type="submit"
          className="px-6 py-3.5 bg-blue-600 hover:bg-blue-700 text-white font-bold text-sm rounded-2xl shadow-md active:scale-95 transition-all"
        >
          Track
        </button>
      </form>

      {/* Searched Application Result */}
      {searchedApp ? (
        <div className="bg-white rounded-3xl border border-slate-200/90 shadow-xl overflow-hidden p-6 sm:p-8 animate-in fade-in duration-300">
          
          {/* Status Header */}
          <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 pb-6 border-b border-slate-200">
            <div>
              <div className="flex items-center gap-2">
                <span className="font-mono text-base font-bold text-blue-700">
                  {searchedApp.applicationId}
                </span>
                <span className={`text-[11px] font-bold px-2.5 py-0.5 rounded-full uppercase ${
                  searchedApp.status === 'Completed'
                    ? 'bg-emerald-100 text-emerald-800'
                    : searchedApp.status === 'Rejected'
                    ? 'bg-red-100 text-red-800'
                    : 'bg-amber-100 text-amber-800'
                }`}>
                  {searchedApp.status}
                </span>
              </div>
              <h3 className="text-xl font-extrabold text-slate-900 mt-1">
                {searchedApp.serviceName}
              </h3>
              <p className="text-xs text-slate-500">
                Applicant: <strong>{searchedApp.customerName}</strong> ({searchedApp.customerPhone})
              </p>
            </div>

            <div className="flex items-center gap-2">
              <button
                onClick={() => onViewReceipt(searchedApp.applicationId)}
                className="flex items-center gap-1.5 px-3.5 py-2 text-xs font-bold text-slate-700 bg-slate-100 hover:bg-slate-200 rounded-xl transition-colors"
              >
                <Receipt className="w-3.5 h-3.5 text-blue-600" />
                <span>View Receipt</span>
              </button>
              <a
                href={`https://wa.me/919876543210?text=Hi%20TT%20Online%20Services,%20inquiring%20about%20my%20application%20${searchedApp.applicationId}`}
                target="_blank"
                rel="noreferrer"
                className="flex items-center gap-1.5 px-3.5 py-2 text-xs font-bold text-emerald-800 bg-emerald-100 hover:bg-emerald-200 rounded-xl transition-colors"
              >
                <MessageCircle className="w-3.5 h-3.5 text-emerald-600" />
                <span>Operator Chat</span>
              </a>
            </div>
          </div>

          {/* Progress Timeline */}
          <div className="py-8">
            <h4 className="text-xs font-bold text-slate-700 uppercase tracking-wider mb-6">
              Application Milestones
            </h4>
            
            <div className="grid grid-cols-1 sm:grid-cols-4 gap-4 relative">
              
              {/* Step 1 */}
              <div className="flex items-start sm:flex-col gap-3">
                <div className="w-8 h-8 rounded-full bg-emerald-600 text-white flex items-center justify-center font-bold text-xs flex-shrink-0 shadow-md shadow-emerald-500/20">
                  ✓
                </div>
                <div>
                  <p className="text-xs font-bold text-slate-900">Application Submitted</p>
                  <p className="text-[11px] text-slate-500">Payment received via Razorpay</p>
                </div>
              </div>

              {/* Step 2 */}
              <div className="flex items-start sm:flex-col gap-3">
                <div className={`w-8 h-8 rounded-full flex items-center justify-center font-bold text-xs flex-shrink-0 ${
                  getStepState(searchedApp.status, 1) === 'completed'
                    ? 'bg-emerald-600 text-white'
                    : getStepState(searchedApp.status, 1) === 'current'
                    ? 'bg-blue-600 text-white animate-pulse'
                    : 'bg-slate-200 text-slate-500'
                }`}>
                  {getStepState(searchedApp.status, 1) === 'completed' ? '✓' : '2'}
                </div>
                <div>
                  <p className="text-xs font-bold text-slate-900">Document Audit</p>
                  <p className="text-[11px] text-slate-500">Operator checking spelling & proofs</p>
                </div>
              </div>

              {/* Step 3 */}
              <div className="flex items-start sm:flex-col gap-3">
                <div className={`w-8 h-8 rounded-full flex items-center justify-center font-bold text-xs flex-shrink-0 ${
                  getStepState(searchedApp.status, 2) === 'completed'
                    ? 'bg-emerald-600 text-white'
                    : getStepState(searchedApp.status, 2) === 'current'
                    ? 'bg-blue-600 text-white animate-pulse'
                    : 'bg-slate-200 text-slate-500'
                }`}>
                  {getStepState(searchedApp.status, 2) === 'completed' ? '✓' : '3'}
                </div>
                <div>
                  <p className="text-xs font-bold text-slate-900">Govt Portal Filing</p>
                  <p className="text-[11px] text-slate-500">Submitted to official authority</p>
                </div>
              </div>

              {/* Step 4 */}
              <div className="flex items-start sm:flex-col gap-3">
                <div className={`w-8 h-8 rounded-full flex items-center justify-center font-bold text-xs flex-shrink-0 ${
                  searchedApp.status === 'Completed'
                    ? 'bg-emerald-600 text-white'
                    : searchedApp.status === 'Rejected'
                    ? 'bg-red-600 text-white'
                    : 'bg-slate-200 text-slate-500'
                }`}>
                  {searchedApp.status === 'Completed' ? '✓' : searchedApp.status === 'Rejected' ? '✕' : '4'}
                </div>
                <div>
                  <p className="text-xs font-bold text-slate-900">Dispatched / Completed</p>
                  <p className="text-[11px] text-slate-500">Official certificate issued</p>
                </div>
              </div>

            </div>
          </div>

          {/* Operator Remarks Box */}
          <div className="p-4 bg-slate-50 rounded-2xl border border-slate-200/80">
            <span className="text-[11px] font-bold text-slate-700 uppercase tracking-wider block mb-1">
              Operator Updates & Notes:
            </span>
            <p className="text-xs text-slate-600">
              {searchedApp.adminNotes || 'No specific updates yet. Application is under regular processing.'}
            </p>
          </div>

        </div>
      ) : searchId ? (
        <div className="text-center py-12 bg-white rounded-3xl border border-slate-200 p-8 max-w-lg mx-auto">
          <AlertCircle className="w-12 h-12 text-slate-400 mx-auto mb-3" />
          <h4 className="text-base font-bold text-slate-800">No Application Found</h4>
          <p className="text-xs text-slate-500 mt-1 mb-4">
            Could not find an application with reference ID or phone number matching "{searchId}".
          </p>
          <button
            onClick={onApplyNew}
            className="px-5 py-2.5 bg-blue-600 text-white font-bold text-xs rounded-xl hover:bg-blue-700"
          >
            Apply for a Service
          </button>
        </div>
      ) : null}

      {/* User's recent applications list if logged in */}
      {applications.length > 0 && !searchedApp && (
        <div className="mt-8">
          <h3 className="text-lg font-bold text-slate-900 mb-4">
            Your Recent Applications ({applications.length})
          </h3>
          <div className="space-y-3">
            {applications.map((app) => (
              <div
                key={app.applicationId}
                onClick={() => setSearchedApp(app)}
                className="p-4 bg-white rounded-2xl border border-slate-200 hover:border-blue-400 cursor-pointer shadow-sm flex items-center justify-between transition-all"
              >
                <div>
                  <div className="flex items-center gap-2">
                    <span className="font-mono text-xs font-bold text-blue-700">{app.applicationId}</span>
                    <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-slate-100 text-slate-800">
                      {app.status}
                    </span>
                  </div>
                  <h4 className="font-bold text-slate-900 text-sm mt-0.5">{app.serviceName}</h4>
                  <p className="text-xs text-slate-500">{app.customerName} • ₹{app.amountPaid}</p>
                </div>
                <ChevronRight className="w-5 h-5 text-slate-400" />
              </div>
            ))}
          </div>
        </div>
      )}

    </div>
  );
};
