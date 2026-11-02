import React from 'react';
import { FileText, ShieldCheck, Phone, Mail, ExternalLink, Heart } from 'lucide-react';
import { ALL_SERVICES } from '../data/services';

interface FooterProps {
  onSelectService: (serviceId: string) => void;
  onNavigate: (tab: string) => void;
}

export const Footer: React.FC<FooterProps> = ({ onSelectService, onNavigate }) => {
  return (
    <footer className="bg-slate-950 text-slate-300 text-xs border-t border-slate-800">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-8">
          
          {/* Brand Info */}
          <div className="lg:col-span-2 space-y-4">
            <div className="flex items-center space-x-3">
              <div className="w-10 h-10 rounded-xl bg-blue-600 flex items-center justify-center text-white font-black text-lg shadow-md shadow-blue-500/20">
                TT
              </div>
              <span className="font-extrabold text-xl text-white tracking-tight">
                TT Online Services
              </span>
            </div>
            <p className="text-slate-400 text-xs leading-relaxed max-w-sm">
              India's professional citizen document assistance portal. We facilitate end-to-end paperwork, pre-audit verifications, and online portal filings for PAN, Aadhaar appointments, Voter IDs, DL, and Government Revenue Certificates.
            </p>
            <div className="pt-2 text-slate-500 text-[11px] space-y-1">
              <p>Helpline: +91 98765 43210 (Mon-Sat, 9 AM - 7:30 PM)</p>
              <p>Email: support@ttonlineservices.com</p>
            </div>
          </div>

          {/* Services Column 1 */}
          <div>
            <h4 className="text-white font-bold text-xs uppercase tracking-wider mb-3">
              Identity & Citizenship
            </h4>
            <ul className="space-y-2">
              {ALL_SERVICES.slice(0, 5).map((srv) => (
                <li key={srv.id}>
                  <button
                    onClick={() => onSelectService(srv.id)}
                    className="hover:text-blue-400 text-left transition-colors text-slate-400 hover:underline"
                  >
                    {srv.title}
                  </button>
                </li>
              ))}
            </ul>
          </div>

          {/* Services Column 2 */}
          <div>
            <h4 className="text-white font-bold text-xs uppercase tracking-wider mb-3">
              Revenue & Certificates
            </h4>
            <ul className="space-y-2">
              {ALL_SERVICES.slice(5).map((srv) => (
                <li key={srv.id}>
                  <button
                    onClick={() => onSelectService(srv.id)}
                    className="hover:text-blue-400 text-left transition-colors text-slate-400 hover:underline"
                  >
                    {srv.title}
                  </button>
                </li>
              ))}
            </ul>
          </div>

          {/* Portal Navigation */}
          <div>
            <h4 className="text-white font-bold text-xs uppercase tracking-wider mb-3">
              Quick Links
            </h4>
            <ul className="space-y-2">
              <li>
                <button onClick={() => onNavigate('home')} className="hover:text-white text-slate-400">
                  Home
                </button>
              </li>
              <li>
                <button onClick={() => onNavigate('services')} className="hover:text-white text-slate-400">
                  All 9 Services
                </button>
              </li>
              <li>
                <button onClick={() => onNavigate('track')} className="hover:text-white text-slate-400">
                  Track Application
                </button>
              </li>
              <li>
                <button onClick={() => onNavigate('about')} className="hover:text-white text-slate-400">
                  About Us
                </button>
              </li>
              <li>
                <button onClick={() => onNavigate('contact')} className="hover:text-white text-slate-400">
                  Contact & FAQs
                </button>
              </li>
            </ul>
          </div>

        </div>

        {/* Disclaimer Bar */}
        <div className="mt-10 pt-6 border-t border-slate-800 text-[11px] text-slate-500 leading-relaxed text-center sm:text-left space-y-2">
          <p>
            <strong>Statutory Disclosure & Disclaimer:</strong> TT Online Services is an independent citizen facilitation centre and document consultancy. We are not affiliated with, authorized by, or an official entity of the Government of India or any state government. All trademarks, logos, and portal names (NSDL, UTIITSL, UIDAI, ECI, Sarathi Parivahan, CRS) are property of their respective government owners. Our service fee covers expert document verification, form filling, operator assistance, and customer tracking services.
          </p>
          <div className="flex flex-col sm:flex-row justify-between items-center pt-4 border-t border-slate-900 text-slate-600">
            <p>© {new Date().getFullYear()} TT Online Services. All rights reserved.</p>
            <p className="flex items-center gap-1 mt-2 sm:mt-0">
              <span>Secure Razorpay Live 256-Bit SSL Payments</span>
            </p>
          </div>
        </div>

      </div>
    </footer>
  );
};
