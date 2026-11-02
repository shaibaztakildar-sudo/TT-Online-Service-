import React from 'react';
import {
  Clock,
  ExternalLink,
  CheckCircle,
  FileText,
  CreditCard,
  ChevronRight,
  ShieldAlert
} from 'lucide-react';
import { DocumentService } from '../types';

interface ServiceCardProps {
  service: DocumentService;
  onApply: (service: DocumentService) => void;
  onViewDetails: (service: DocumentService) => void;
}

export const ServiceCard: React.FC<ServiceCardProps> = ({
  service,
  onApply,
  onViewDetails
}) => {
  return (
    <div className="bg-white rounded-2xl border border-slate-200/90 shadow-sm hover:shadow-md hover:border-blue-400 transition-all duration-200 flex flex-col justify-between overflow-hidden group">
      
      {/* Top Banner & Category */}
      <div className="p-6 pb-4">
        <div className="flex items-center justify-between gap-2 mb-3">
          <span className="text-[11px] font-bold uppercase tracking-wider px-2.5 py-1 rounded-full bg-blue-50 text-blue-700 border border-blue-200/60">
            {service.category}
          </span>
          <div className="flex items-center text-xs font-semibold text-slate-500 gap-1 bg-slate-50 px-2.5 py-1 rounded-full">
            <Clock className="w-3.5 h-3.5 text-amber-500" />
            <span>{service.turnaroundTime}</span>
          </div>
        </div>

        {/* Title & Subtitle */}
        <h3 className="text-lg font-bold text-slate-900 group-hover:text-blue-700 transition-colors">
          {service.title}
        </h3>
        <p className="text-xs font-medium text-slate-500 mt-1 mb-3">
          {service.subtitle}
        </p>

        {/* Short Description */}
        <p className="text-xs text-slate-600 line-clamp-2 leading-relaxed">
          {service.description}
        </p>

        {/* Required Documents Pill Preview */}
        <div className="mt-4 pt-4 border-t border-slate-100">
          <p className="text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-2 flex items-center gap-1.5">
            <CheckCircle className="w-3.5 h-3.5 text-emerald-500" />
            <span>Key Documents Needed:</span>
          </p>
          <ul className="space-y-1.5">
            {service.checklist.slice(0, 3).map((doc, idx) => (
              <li key={idx} className="text-xs text-slate-600 flex items-start gap-1.5 line-clamp-1">
                <span className="w-1.5 h-1.5 rounded-full bg-blue-500 mt-1.5 flex-shrink-0"></span>
                <span>{doc}</span>
              </li>
            ))}
            {service.checklist.length > 3 && (
              <li className="text-[11px] font-semibold text-blue-600 pl-3">
                +{service.checklist.length - 3} more required items
              </li>
            )}
          </ul>
        </div>
      </div>

      {/* Card Footer with Price and Actions */}
      <div className="bg-slate-50/80 px-6 py-4 border-t border-slate-100 flex items-center justify-between gap-3">
        <div>
          <span className="text-[11px] text-slate-500 font-medium block">Facilitation Fee</span>
          <div className="flex items-baseline gap-1">
            <span className="text-xl font-extrabold text-slate-900">₹{service.feeInr}</span>
            <span className="text-[10px] text-slate-400 font-normal">incl. GST</span>
          </div>
        </div>

        <div className="flex items-center gap-2">
          <button
            onClick={() => onViewDetails(service)}
            className="px-3 py-2 text-xs font-bold text-slate-700 hover:text-blue-700 hover:bg-white rounded-xl border border-slate-200 transition-colors"
          >
            Checklist
          </button>
          <button
            onClick={() => onApply(service)}
            className="px-4 py-2 text-xs font-bold bg-blue-600 hover:bg-blue-700 text-white rounded-xl shadow-sm hover:shadow active:scale-95 transition-all flex items-center gap-1"
          >
            <span>Apply Now</span>
            <ChevronRight className="w-3.5 h-3.5" />
          </button>
        </div>
      </div>

    </div>
  );
};
