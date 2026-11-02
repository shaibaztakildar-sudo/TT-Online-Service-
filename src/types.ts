export interface UserProfile {
  id: string;
  email: string;
  name: string;
  phone?: string;
  role: 'customer' | 'admin';
  createdAt?: any;
  updatedAt?: any;
}

export type ApplicationStatus =
  | 'Pending'
  | 'Under Review'
  | 'Processing'
  | 'Completed'
  | 'Rejected'
  | 'Additional Information Required';

export type PaymentStatus = 'Pending' | 'Paid' | 'Failed' | 'Refunded';

export interface ServiceApplication {
  applicationId: string;
  userId: string;
  customerName: string;
  customerEmail: string;
  customerPhone: string;
  serviceId: string;
  serviceName: string;
  applicationType: string;
  formData: string; // JSON string with all custom fields
  status: ApplicationStatus;
  paymentStatus: PaymentStatus;
  paymentId?: string;
  paymentOrderId?: string;
  amountPaid: number;
  consentGiven: boolean;
  documentCount: number;
  adminNotes?: string;
  additionalInfoRequested?: string;
  createdAt?: any;
  updatedAt?: any;
}

export interface PaymentRecord {
  paymentId: string;
  orderId: string;
  applicationId: string;
  userId: string;
  amount: number;
  currency: string;
  status: 'SUCCESS' | 'PENDING' | 'FAILED';
  razorpayPaymentId: string;
  razorpaySignature?: string;
  receiptNumber: string;
  serviceName: string;
  createdAt?: any;
}

export interface AppNotification {
  notificationId: string;
  userId: string;
  title: string;
  message: string;
  applicationId?: string;
  read: boolean;
  createdAt?: any;
}

export interface DocumentService {
  id: string;
  title: string;
  subtitle: string;
  category: string;
  feeInr: number;
  turnaroundTime: string;
  officialPortal: string;
  description: string;
  instructions: string[];
  checklist: string[];
  applicationTypes: string[];
}
