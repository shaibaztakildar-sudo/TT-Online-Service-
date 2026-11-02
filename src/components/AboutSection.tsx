import React from 'react';
import { ShieldCheck, Award, Users, CheckCircle, Building2, HelpCircle } from 'lucide-react';

export const AboutSection: React.FC = () => {
  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-14 space-y-12">
      
      {/* Intro Heading */}
      <div className="text-center max-w-3xl mx-auto space-y-3">
        <span className="text-xs font-bold text-blue-600 bg-blue-50 px-3 py-1 rounded-full uppercase tracking-wider">
          About TT Online Services
        </span>
        <h2 className="text-3xl sm:text-4xl font-extrabold text-slate-900">
          Simplifying Indian Document Procedures for Every Citizen
        </h2>
        <p className="text-sm text-slate-600 leading-relaxed">
          TT Online Services was founded to bridge the gap between complex government documentation requirements and everyday citizens. Our certified documentation specialists eliminate rejection risks, clarify paperwork rules, and expedite official submissions.
        </p>
      </div>

      {/* 3 Pillars */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="p-6 bg-white rounded-3xl border border-slate-200 shadow-sm space-y-3">
          <div className="w-12 h-12 rounded-2xl bg-blue-100 text-blue-700 flex items-center justify-center">
            <ShieldCheck className="w-6 h-6" />
          </div>
          <h3 className="font-bold text-lg text-slate-900">Privacy & Data Security</h3>
          <p className="text-xs text-slate-600 leading-relaxed">
            Your personal identity proofs, Aadhaar details, and photographs are processed over 256-bit encrypted channels and never shared with unauthorized third parties.
          </p>
        </div>

        <div className="p-6 bg-white rounded-3xl border border-slate-200 shadow-sm space-y-3">
          <div className="w-12 h-12 rounded-2xl bg-emerald-100 text-emerald-700 flex items-center justify-center">
            <Award className="w-6 h-6" />
          </div>
          <h3 className="font-bold text-lg text-slate-900">Expert Pre-Audit</h3>
          <p className="text-xs text-slate-600 leading-relaxed">
            Every application undergoes a stringent multi-point audit by experienced operators before final filing on NSDL, UIDAI, ECI, Parivahan, or State e-District portals.
          </p>
        </div>

        <div className="p-6 bg-white rounded-3xl border border-slate-200 shadow-sm space-y-3">
          <div className="w-12 h-12 rounded-2xl bg-amber-100 text-amber-700 flex items-center justify-center">
            <Building2 className="w-6 h-6" />
          </div>
          <h3 className="font-bold text-lg text-slate-900">Authorized Channels</h3>
          <p className="text-xs text-slate-600 leading-relaxed">
            All government fees are remitted through certified state revenue gateways, ensuring official government receipts and authentic delivery via India Post.
          </p>
        </div>
      </div>

      {/* Statutory Disclaimer Card */}
      <div className="p-6 bg-slate-900 text-slate-300 rounded-3xl border border-slate-800 space-y-3">
        <h4 className="text-amber-400 font-bold text-sm uppercase tracking-wider flex items-center gap-2">
          <HelpCircle className="w-4 h-4" />
          <span>Statutory Transparency & Non-Government Disclosure</span>
        </h4>
        <p className="text-xs leading-relaxed text-slate-300">
          TT Online Services (operated by certified digital seva professionals) is a private document consultancy and citizen facilitation platform. We do not manufacture or issue government certificates directly; sovereign authority for approval and issuance rests exclusively with respective government authorities (e.g., Ministry of Finance / Income Tax Department for PAN, Election Commission of India for EPIC, UIDAI for Aadhaar, State Transport Departments for Driving Licences, and District Revenue Officers for Certificates).
        </p>
      </div>

    </div>
  );
};
