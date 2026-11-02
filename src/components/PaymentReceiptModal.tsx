import React from 'react';
import {
  X,
  Printer,
  Download,
  CheckCircle2,
  ShieldCheck,
  Building,
  Receipt,
  ArrowRight
} from 'lucide-react';
import { PaymentRecord, ServiceApplication } from '../types';

interface PaymentReceiptModalProps {
  payment: PaymentRecord | null;
  application: ServiceApplication | null;
  onClose: () => void;
  onTrack: (appId: string) => void;
}

export const PaymentReceiptModal: React.FC<PaymentReceiptModalProps> = ({
  payment,
  application,
  onClose,
  onTrack
}) => {
  if (!payment) return null;

  const handlePrint = () => {
    window.print();
  };

  const formattedDate = payment.createdAt?.toDate
    ? payment.createdAt.toDate().toLocaleString('en-IN')
    : new Date().toLocaleString('en-IN');

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
      <div className="bg-white rounded-3xl max-w-xl w-full overflow-hidden shadow-2xl border border-slate-200 animate-in fade-in zoom-in-95 duration-200">
        
        {/* Top Control Bar (Hidden during print) */}
        <div className="bg-slate-900 text-white px-6 py-4 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Receipt className="w-5 h-5 text-emerald-400" />
            <span className="font-bold text-sm">Official Payment Receipt</span>
          </div>
          <div className="flex items-center gap-2">
            <button
              onClick={handlePrint}
              className="flex items-center gap-1.5 px-3 py-1.5 bg-blue-600 hover:bg-blue-500 text-white text-xs font-bold rounded-lg transition-colors"
            >
              <Printer className="w-3.5 h-3.5" />
              <span>Print / Save PDF</span>
            </button>
            <button
              onClick={onClose}
              className="p-1.5 text-slate-400 hover:text-white rounded-full hover:bg-slate-800 transition-colors"
            >
              <X className="w-5 h-5" />
            </button>
          </div>
        </div>

        {/* Printable Receipt Body */}
        <div id="printable-receipt" className="p-8 space-y-6 bg-white text-slate-800">
          
          {/* Header */}
          <div className="flex justify-between items-start border-b border-slate-200 pb-5">
            <div>
              <div className="flex items-center gap-2">
                <div className="w-9 h-9 rounded-lg bg-blue-700 text-white flex items-center justify-center font-black text-base shadow-sm">
                  TT
                </div>
                <div>
                  <h1 className="font-black text-xl text-slate-900 tracking-tight leading-none">
                    TT Online Services
                  </h1>
                  <span className="text-[10px] text-slate-500 font-semibold uppercase tracking-wider">
                    Digital Document Assistance Portal
                  </span>
                </div>
              </div>
              <p className="text-xs text-slate-500 mt-2">
                Govt Document Facilitation Center<br />
                GSTIN / Enrolment Ref: 27AABCT8901K1ZK
              </p>
            </div>

            <div className="text-right">
              <span className="inline-block bg-emerald-100 text-emerald-800 text-xs font-black px-3 py-1 rounded-full uppercase tracking-wider mb-1">
                PAID & VERIFIED
              </span>
              <p className="text-xs text-slate-500">Receipt No: <strong className="text-slate-900">{payment.receiptNumber}</strong></p>
              <p className="text-xs text-slate-500">Date: {formattedDate}</p>
            </div>
          </div>

          {/* Details Table */}
          <div className="grid grid-cols-2 gap-4 text-xs bg-slate-50 p-4 rounded-2xl border border-slate-200">
            <div>
              <span className="text-slate-500 block text-[11px] font-semibold uppercase">Customer Name</span>
              <span className="font-bold text-slate-900 text-sm">
                {application?.customerName || 'Authorized Applicant'}
              </span>
            </div>
            <div>
              <span className="text-slate-500 block text-[11px] font-semibold uppercase">Application Ref ID</span>
              <span className="font-mono font-bold text-blue-700 text-sm">
                {payment.applicationId}
              </span>
            </div>
            <div>
              <span className="text-slate-500 block text-[11px] font-semibold uppercase">Contact Email / Phone</span>
              <span className="font-medium text-slate-700">
                {application?.customerEmail || '-'} | {application?.customerPhone || '-'}
              </span>
            </div>
            <div>
              <span className="text-slate-500 block text-[11px] font-semibold uppercase">Payment Gateway</span>
              <span className="font-medium text-slate-700">
                Razorpay Live ({payment.razorpayPaymentId})
              </span>
            </div>
          </div>

          {/* Items breakdown */}
          <div>
            <table className="w-full text-left text-xs border border-slate-200 rounded-xl overflow-hidden">
              <thead className="bg-slate-100 text-slate-700 uppercase font-bold text-[10px]">
                <tr>
                  <th className="p-3">Service Description</th>
                  <th className="p-3 text-center">Category</th>
                  <th className="p-3 text-right">Fee (INR)</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-200">
                <tr>
                  <td className="p-3 font-semibold text-slate-900">
                    {payment.serviceName}
                    <span className="block text-[11px] text-slate-500 font-normal">
                      Document verification, portal filing & tracking facilitation
                    </span>
                  </td>
                  <td className="p-3 text-center text-slate-600">
                    {application?.applicationType || 'Standard'}
                  </td>
                  <td className="p-3 text-right font-bold text-slate-900">
                    ₹{payment.amount.toFixed(2)}
                  </td>
                </tr>
              </tbody>
              <tfoot className="bg-slate-50 font-bold border-t border-slate-200">
                <tr>
                  <td colSpan={2} className="p-3 text-right text-slate-700 uppercase text-xs">
                    Total Amount Paid:
                  </td>
                  <td className="p-3 text-right text-base text-blue-700 font-black">
                    ₹{payment.amount.toFixed(2)}
                  </td>
                </tr>
              </tfoot>
            </table>
          </div>

          {/* Verification Footnote */}
          <div className="pt-2 border-t border-slate-200 text-center space-y-1">
            <div className="flex items-center justify-center gap-1.5 text-xs text-emerald-700 font-bold">
              <ShieldCheck className="w-4 h-4 text-emerald-600" />
              <span>Electronically Authenticated & Recorded in Cloud Database</span>
            </div>
            <p className="text-[10px] text-slate-400">
              This is a computer-generated tax payment receipt. No physical signature required.
            </p>
          </div>

        </div>

        {/* Modal Bottom Actions */}
        <div className="bg-slate-50 px-6 py-4 border-t border-slate-200 flex items-center justify-between gap-4">
          <button
            onClick={onClose}
            className="px-4 py-2 text-xs font-semibold text-slate-600 hover:bg-slate-200 rounded-xl"
          >
            Done
          </button>
          <button
            onClick={() => {
              onClose();
              onTrack(payment.applicationId);
            }}
            className="flex items-center gap-1.5 px-5 py-2.5 bg-blue-600 hover:bg-blue-700 text-white text-xs font-bold rounded-xl shadow-md active:scale-95 transition-all"
          >
            <span>Track This Application</span>
            <ArrowRight className="w-3.5 h-3.5" />
          </button>
        </div>

      </div>
    </div>
  );
};
