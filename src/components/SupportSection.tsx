import React, { useState } from 'react';
import { Phone, Mail, MessageCircle, MapPin, Send, HelpCircle, CheckCircle2 } from 'lucide-react';

export const SupportSection: React.FC = () => {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');
  const [appId, setAppId] = useState('');
  const [message, setMessage] = useState('');
  const [submitted, setSubmitted] = useState(false);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name || !message) {
      alert('Please fill in your name and message.');
      return;
    }
    setSubmitted(true);
    setTimeout(() => {
      setName('');
      setEmail('');
      setPhone('');
      setAppId('');
      setMessage('');
    }, 2000);
  };

  const faqs = [
    {
      q: 'How does TT Online Services process my document?',
      a: 'After you submit your details and fee on our platform, our certified operator reviews all document proofs for errors. We then log into the official government portal (NSDL, UIDAI, ECI, Parivahan, CRS) to submit the formal application and remit the official fees.'
    },
    {
      q: 'How will I receive my physical card or certificate?',
      a: 'Physical documents (like PAN card, Driving Licence, Voter ID) are printed and dispatched directly by the respective government department via India Post Speed Post to your registered postal address.'
    },
    {
      q: 'Can I track the official status on the government portal?',
      a: 'Yes! As soon as our operator files your application on the government portal, we update your TT Online Services dashboard with the official Government Acknowledgment Number / Application Reference Number so you can verify it directly on the government portal as well.'
    },
    {
      q: 'What if my application requires biometric verification?',
      a: 'For services requiring physical biometrics (like Aadhaar card updates or new Driving Licence test), we prepare all verified forms and book an official appointment slot at your nearest authorized Aadhaar Seva Kendra or RTO.'
    }
  ];

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-14 space-y-12">
      
      {/* Title */}
      <div className="text-center max-w-2xl mx-auto space-y-2">
        <span className="text-xs font-bold text-blue-600 bg-blue-50 px-3 py-1 rounded-full uppercase tracking-wider">
          Citizen Helpdesk
        </span>
        <h2 className="text-3xl font-extrabold text-slate-900">
          We Are Here to Assist You
        </h2>
        <p className="text-sm text-slate-500">
          Have questions about required documents, form submissions, or fees? Speak directly with our support team.
        </p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
        
        {/* Left Column: Direct Contact & Details */}
        <div className="lg:col-span-5 space-y-6">
          <div className="bg-gradient-to-br from-blue-900 to-indigo-950 text-white p-6 sm:p-8 rounded-3xl shadow-lg space-y-6">
            <div>
              <h3 className="text-xl font-bold">Contact Channels</h3>
              <p className="text-xs text-slate-300 mt-1">
                Available Monday to Saturday, 9:00 AM – 7:30 PM IST.
              </p>
            </div>

            <div className="space-y-4 text-xs">
              <a
                href="tel:+919876543210"
                className="flex items-center gap-3 p-3 rounded-2xl bg-white/10 hover:bg-white/20 transition-colors"
              >
                <div className="w-9 h-9 rounded-xl bg-blue-500/20 flex items-center justify-center text-blue-300">
                  <Phone className="w-4 h-4" />
                </div>
                <div>
                  <span className="text-[10px] text-slate-400 block uppercase font-bold">Phone Support</span>
                  <span className="font-bold text-sm text-white">+91 98765 43210</span>
                </div>
              </a>

              <a
                href="https://wa.me/919876543210"
                target="_blank"
                rel="noreferrer"
                className="flex items-center gap-3 p-3 rounded-2xl bg-emerald-500/20 hover:bg-emerald-500/30 transition-colors"
              >
                <div className="w-9 h-9 rounded-xl bg-emerald-500/30 flex items-center justify-center text-emerald-300">
                  <MessageCircle className="w-4 h-4" />
                </div>
                <div>
                  <span className="text-[10px] text-emerald-200 block uppercase font-bold">WhatsApp Helpdesk</span>
                  <span className="font-bold text-sm text-white">+91 98765 43210</span>
                </div>
              </a>

              <a
                href="mailto:support@ttonlineservices.com"
                className="flex items-center gap-3 p-3 rounded-2xl bg-white/10 hover:bg-white/20 transition-colors"
              >
                <div className="w-9 h-9 rounded-xl bg-purple-500/20 flex items-center justify-center text-purple-300">
                  <Mail className="w-4 h-4" />
                </div>
                <div>
                  <span className="text-[10px] text-slate-400 block uppercase font-bold">Email Query</span>
                  <span className="font-bold text-sm text-white">support@ttonlineservices.com</span>
                </div>
              </a>

              <div className="flex items-center gap-3 p-3 rounded-2xl bg-white/10">
                <div className="w-9 h-9 rounded-xl bg-amber-500/20 flex items-center justify-center text-amber-300">
                  <MapPin className="w-4 h-4" />
                </div>
                <div>
                  <span className="text-[10px] text-slate-400 block uppercase font-bold">Center Location</span>
                  <span className="font-medium text-xs text-white">TT Online Services Citizen Center, Main Market Road, India</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Right Column: Query Form */}
        <div className="lg:col-span-7 bg-white p-6 sm:p-8 rounded-3xl border border-slate-200 shadow-sm space-y-5">
          <h3 className="text-lg font-bold text-slate-900">
            Send an Inquiry or Document Assistance Request
          </h3>

          {submitted ? (
            <div className="p-6 bg-emerald-50 border border-emerald-200 rounded-2xl text-center space-y-2">
              <CheckCircle2 className="w-10 h-10 text-emerald-600 mx-auto" />
              <h4 className="font-bold text-sm text-emerald-900">Inquiry Received</h4>
              <p className="text-xs text-emerald-700">
                Thank you! Our customer support representative will get back to you via WhatsApp or phone shortly.
              </p>
            </div>
          ) : (
            <form onSubmit={handleSubmit} className="space-y-4 text-xs">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="font-bold text-slate-700 uppercase block mb-1">Your Name *</label>
                  <input
                    type="text"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    placeholder="Full Name"
                    required
                    className="w-full px-3.5 py-2.5 rounded-xl border border-slate-300 text-slate-900 text-xs focus:ring-2 focus:ring-blue-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="font-bold text-slate-700 uppercase block mb-1">Phone Number (WhatsApp) *</label>
                  <input
                    type="tel"
                    value={phone}
                    onChange={(e) => setPhone(e.target.value)}
                    placeholder="10-digit mobile"
                    required
                    className="w-full px-3.5 py-2.5 rounded-xl border border-slate-300 text-slate-900 text-xs focus:ring-2 focus:ring-blue-500 focus:outline-none"
                  />
                </div>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="font-bold text-slate-700 uppercase block mb-1">Email Address</label>
                  <input
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="name@example.com"
                    className="w-full px-3.5 py-2.5 rounded-xl border border-slate-300 text-slate-900 text-xs focus:ring-2 focus:ring-blue-500 focus:outline-none"
                  />
                </div>
                <div>
                  <label className="font-bold text-slate-700 uppercase block mb-1">Application ID (if already applied)</label>
                  <input
                    type="text"
                    value={appId}
                    onChange={(e) => setAppId(e.target.value)}
                    placeholder="e.g. TT-123456-7890"
                    className="w-full px-3.5 py-2.5 rounded-xl border border-slate-300 text-slate-900 text-xs focus:ring-2 focus:ring-blue-500 focus:outline-none font-mono"
                  />
                </div>
              </div>

              <div>
                <label className="font-bold text-slate-700 uppercase block mb-1">Your Question or Service Requirement *</label>
                <textarea
                  value={message}
                  onChange={(e) => setMessage(e.target.value)}
                  placeholder="Describe what help you need with your document or application..."
                  rows={4}
                  required
                  className="w-full px-3.5 py-2.5 rounded-xl border border-slate-300 text-slate-900 text-xs focus:ring-2 focus:ring-blue-500 focus:outline-none"
                ></textarea>
              </div>

              <button
                type="submit"
                className="w-full sm:w-auto px-6 py-3 bg-blue-600 hover:bg-blue-700 text-white font-bold rounded-xl shadow-md flex items-center justify-center gap-2 active:scale-95 transition-all text-xs"
              >
                <Send className="w-3.5 h-3.5" />
                <span>Submit Inquiry</span>
              </button>
            </form>
          )}
        </div>

      </div>

      {/* Frequently Asked Questions */}
      <div className="pt-8">
        <h3 className="text-xl font-bold text-slate-900 text-center mb-6">
          Frequently Asked Questions
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 max-w-5xl mx-auto">
          {faqs.map((faq, idx) => (
            <div key={idx} className="p-5 bg-white rounded-2xl border border-slate-200 shadow-sm space-y-2">
              <h4 className="font-bold text-sm text-slate-900 flex items-start gap-2">
                <HelpCircle className="w-4 h-4 text-blue-600 flex-shrink-0 mt-0.5" />
                <span>{faq.q}</span>
              </h4>
              <p className="text-xs text-slate-600 leading-relaxed pl-6">
                {faq.a}
              </p>
            </div>
          ))}
        </div>
      </div>

    </div>
  );
};
