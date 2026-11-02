import React from 'react';
import {
  X,
  Clock,
  ExternalLink,
  ShieldCheck,
  CheckCircle2,
  FileText,
  AlertTriangle,
  ArrowRight
} from 'lucide-react';
import { DocumentService } from '../types';

interface ServiceDetailModalProps {
  service: DocumentService | null;
  onClose: () => void;
  onApply: (service: DocumentService) => void;
}

export const ServiceDetailModal: React.FC<ServiceDetailModalProps> = ({
  service,
  onClose,
  onApply
}) => {
  if (!service) return null;

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
      <div className="bg-white rounded-3xl max-w-2xl w-full max-h-[90vh] overflow-y-auto shadow-2xl border border-slate-200 animate-in fade-in zoom-in-95 duration-200">
        
        {/* Header */}
        <div className="sticky top-0 bg-white/95 backdrop-blur-md px-6 py-5 border-b border-slate-200 flex items-center justify-between z-10">
          <div>
            <span className="text-[11px] font-bold text-blue-700 uppercase tracking-wider bg-blue-50 px-2 py-0.5 rounded">
              {service.category}
            </span>
            <h2 className="text-xl font-extrabold text-slate-900 mt-1">
              {service.title}
            </h2>
          </div>
          <button
            onClick={onClose}
            className="p-2 text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-full transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Content */}
        <div className="p-6 space-y-6">
          
          {/* Key Facts bar */}
          <div className="grid grid-cols-2 sm:grid-cols-3 gap-3 p-4 bg-slate-50 rounded-2xl border border-slate-200/80">
            <div>
              <span className="text-[11px] text-slate-500 font-medium block">Facilitation Fee</span>
              <span className="text-lg font-black text-slate-900">₹{service.feeInr}</span>
            </div>
            <div>
              <span className="text-[11px] text-slate-500 font-medium block">Turnaround Time</span>
              <span className="text-sm font-bold text-slate-800 flex items-center gap-1">
                <Clock className="w-3.5 h-3.5 text-amber-500" />
                {service.turnaroundTime}
              </span>
            </div>
            <div className="col-span-2 sm:col-span-1">
              <span className="text-[11px] text-slate-500 font-medium block">Official Authority</span>
              <span className="text-xs font-bold text-slate-800 truncate block">
                {service.officialPortal}
              </span>
            </div>
          </div>

          {/* Description */}
          <div>
            <h3 className="text-sm font-bold text-slate-900 uppercase tracking-wide mb-2">
              Service Scope & Process
            </h3>
            <p className="text-sm text-slate-600 leading-relaxed">
              {service.description}
            </p>
          </div>

          {/* Application Types */}
          <div>
            <h3 className="text-sm font-bold text-slate-900 uppercase tracking-wide mb-2">
              Available Application Categories
            </h3>
            <div className="flex flex-wrap gap-2">
              {service.applicationTypes.map((type, idx) => (
                <span
                  key={idx}
                  className="px-3 py-1.5 rounded-xl bg-slate-100 text-slate-800 text-xs font-semibold border border-slate-200"
                >
                  {type}
                </span>
              ))}
            </div>
          </div>

          {/* Complete Required Documents Checklist */}
          <div>
            <h3 className="text-sm font-bold text-slate-900 uppercase tracking-wide mb-2 flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 text-emerald-600" />
              <span>Mandatory Document Checklist</span>
            </h3>
            <div className="bg-emerald-50/50 border border-emerald-200/60 rounded-2xl p-4 space-y-2.5">
              {service.checklist.map((item, idx) => (
                <div key={idx} className="flex items-start gap-2 text-xs text-slate-700">
                  <CheckCircle2 className="w-4 h-4 text-emerald-600 flex-shrink-0 mt-0.5" />
                  <span className="font-medium">{item}</span>
                </div>
              ))}
            </div>
          </div>

          {/* Procedural Instructions */}
          <div>
            <h3 className="text-sm font-bold text-slate-900 uppercase tracking-wide mb-2">
              Important Applicant Instructions
            </h3>
            <ul className="space-y-2">
              {service.instructions.map((inst, idx) => (
                <li key={idx} className="flex items-start gap-2 text-xs text-slate-600">
                  <span className="font-bold text-blue-600 flex-shrink-0">{idx + 1}.</span>
                  <span>{inst}</span>
                </li>
              ))}
            </ul>
          </div>

          {/* Disclaimer Banner */}
          <div className="p-4 bg-amber-50 border border-amber-200 rounded-2xl flex items-start gap-3">
            <AlertTriangle className="w-5 h-5 text-amber-600 flex-shrink-0 mt-0.5" />
            <p className="text-xs text-amber-900 leading-relaxed">
              <strong>Statutory Disclosure:</strong> TT Online Services is not a government agency. We act as an authorized citizen facilitation centre assisting clients with accurate paperwork, portal submission, fee processing, and tracking.
            </p>
          </div>

        </div>

        {/* Modal Footer */}
        <div className="sticky bottom-0 bg-slate-50 px-6 py-4 border-t border-slate-200 flex items-center justify-between gap-4">
          <button
            onClick={onClose}
            className="px-5 py-2.5 text-sm font-semibold text-slate-700 hover:bg-slate-200 rounded-xl transition-colors"
          >
            Close
          </button>
          <button
            onClick={() => {
              onClose();
              onApply(service);
            }}
            className="flex items-center gap-2 px-6 py-2.5 bg-blue-600 hover:bg-blue-700 text-white text-sm font-bold rounded-xl shadow-md active:scale-95 transition-all"
          >
            <span>Proceed to Apply (₹{service.feeInr})</span>
            <ArrowRight className="w-4 h-4" />
          </button>
        </div>

      </div>
    </div>
  );
};
