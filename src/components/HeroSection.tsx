import React from 'react';
import {
  ShieldCheck,
  Search,
  CheckCircle2,
  Clock,
  ArrowRight,
  CreditCard,
  FileCheck2,
  Headphones
} from 'lucide-react';

interface HeroSectionProps {
  onSearchChange: (query: string) => void;
  searchQuery: string;
  onExploreServices: () => void;
  onTrackClick: () => void;
}

export const HeroSection: React.FC<HeroSectionProps> = ({
  onSearchChange,
  searchQuery,
  onExploreServices,
  onTrackClick
}) => {
  return (
    <div className="relative overflow-hidden bg-gradient-to-b from-blue-900 via-indigo-950 to-slate-900 text-white pt-12 pb-16 px-4 sm:px-6 lg:px-8">
      {/* Decorative background glow */}
      <div className="absolute top-0 left-1/2 -translate-x-1/2 w-[800px] h-[350px] bg-blue-500/10 rounded-full blur-3xl pointer-events-none"></div>
      
      <div className="max-w-7xl mx-auto relative z-10">
        
        {/* Badge */}
        <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-blue-500/10 border border-blue-400/20 text-blue-200 text-xs font-semibold mb-6 backdrop-blur-md">
          <ShieldCheck className="w-4 h-4 text-emerald-400" />
          <span>Trusted Digital Seva & Document Consultancy Portal</span>
        </div>

        {/* Heading & Subheading */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 items-center">
          <div className="lg:col-span-7 text-left space-y-6">
            <h1 className="text-3xl sm:text-4xl md:text-5xl font-extrabold tracking-tight leading-tight text-white">
              Hassle-Free Indian Document Assistance at Your Fingertips.
            </h1>
            <p className="text-base sm:text-lg text-slate-300 font-normal leading-relaxed max-w-2xl">
              Professional, prompt, and error-free facilitation for PAN cards, Voter IDs, Aadhaar appointments, Driving Licences, Passports, and State Revenue Certificates.
            </p>

            {/* Live Search Box */}
            <div className="pt-2 max-w-xl">
              <div className="relative flex items-center">
                <Search className="absolute left-4 w-5 h-5 text-slate-400" />
                <input
                  type="text"
                  value={searchQuery}
                  onChange={(e) => onSearchChange(e.target.value)}
                  placeholder="Search service (e.g. PAN card, Driving licence, Domicile)..."
                  className="w-full pl-12 pr-4 py-3.5 rounded-2xl bg-white/10 hover:bg-white/15 focus:bg-white text-white focus:text-slate-900 placeholder:text-slate-400 text-sm font-medium border border-white/20 focus:border-blue-500 focus:outline-none focus:ring-4 focus:ring-blue-500/20 shadow-lg backdrop-blur-md transition-all"
                />
              </div>
            </div>

            {/* CTA Buttons */}
            <div className="flex flex-wrap items-center gap-4 pt-2">
              <button
                onClick={onExploreServices}
                className="flex items-center gap-2 bg-blue-600 hover:bg-blue-500 text-white font-bold px-6 py-3.5 rounded-xl shadow-lg shadow-blue-600/30 active:scale-95 transition-all text-sm"
              >
                <span>Browse All 9 Services</span>
                <ArrowRight className="w-4 h-4" />
              </button>
              <button
                onClick={onTrackClick}
                className="flex items-center gap-2 bg-white/10 hover:bg-white/20 text-white font-semibold px-5 py-3.5 rounded-xl border border-white/20 active:scale-95 transition-all text-sm"
              >
                <span>Track Application</span>
              </button>
            </div>

            {/* Legal Notice */}
            <div className="p-3 bg-amber-500/10 border border-amber-500/20 rounded-xl max-w-2xl text-xs text-amber-200/90 leading-relaxed">
              <strong className="text-amber-300 font-semibold">Important Transparency Notice:</strong> TT Online Services is an independent citizen facilitation platform operated by certified operators. We prepare, audit, and submit client applications directly on respective official government portals (such as NSDL, UTIITSL, UIDAI, ECI, Parivahan, and State e-Districts).
            </div>
          </div>

          {/* Feature Highlights Grid */}
          <div className="lg:col-span-5 grid grid-cols-1 sm:grid-cols-2 gap-4">
            
            <div className="p-5 rounded-2xl bg-white/5 border border-white/10 backdrop-blur-md hover:bg-white/10 transition-colors">
              <div className="w-10 h-10 rounded-xl bg-blue-500/20 flex items-center justify-center text-blue-400 mb-3">
                <FileCheck2 className="w-5 h-5" />
              </div>
              <h3 className="font-bold text-white text-base mb-1">Zero Form Rejection</h3>
              <p className="text-xs text-slate-300 leading-normal">
                Operator pre-audits names, spellings, DOB, and document proofs before portal submission.
              </p>
            </div>

            <div className="p-5 rounded-2xl bg-white/5 border border-white/10 backdrop-blur-md hover:bg-white/10 transition-colors">
              <div className="w-10 h-10 rounded-xl bg-emerald-500/20 flex items-center justify-center text-emerald-400 mb-3">
                <CreditCard className="w-5 h-5" />
              </div>
              <h3 className="font-bold text-white text-base mb-1">Razorpay Live (₹)</h3>
              <p className="text-xs text-slate-300 leading-normal">
                100% secure UPI, cards & NetBanking with instant GST/Tax downloadable payment receipts.
              </p>
            </div>

            <div className="p-5 rounded-2xl bg-white/5 border border-white/10 backdrop-blur-md hover:bg-white/10 transition-colors">
              <div className="w-10 h-10 rounded-xl bg-amber-500/20 flex items-center justify-center text-amber-400 mb-3">
                <Clock className="w-5 h-5" />
              </div>
              <h3 className="font-bold text-white text-base mb-1">Fast Turnaround</h3>
              <p className="text-xs text-slate-300 leading-normal">
                Quick 24-48 hour application filing with real-time status tracking and SMS/email updates.
              </p>
            </div>

            <div className="p-5 rounded-2xl bg-white/5 border border-white/10 backdrop-blur-md hover:bg-white/10 transition-colors">
              <div className="w-10 h-10 rounded-xl bg-purple-500/20 flex items-center justify-center text-purple-400 mb-3">
                <Headphones className="w-5 h-5" />
              </div>
              <h3 className="font-bold text-white text-base mb-1">Dedicated Support</h3>
              <p className="text-xs text-slate-300 leading-normal">
                Direct operator support via WhatsApp, phone, and ticket resolution for any query.
              </p>
            </div>

          </div>
        </div>

      </div>
    </div>
  );
};
