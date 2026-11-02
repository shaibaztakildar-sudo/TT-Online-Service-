import { DocumentService } from '../types';

export const ALL_SERVICES: DocumentService[] = [
  {
    id: 'pan_card',
    title: 'PAN Card Assistance',
    subtitle: 'New Application & Correction Support',
    category: 'Identity & Taxation',
    feeInr: 199.0,
    turnaroundTime: '3 to 5 Working Days',
    officialPortal: 'NSDL (Protean) & UTIITSL e-Gov',
    description: 'Get expert assistance for applying for a fresh Permanent Account Number (PAN) or correcting your name, date of birth, father\'s name, or photograph on your existing PAN card.',
    instructions: [
      'Ensure applicant name exactly matches Aadhaar or valid identity proof.',
      'For correction, keep your existing 10-digit PAN number handy.',
      'Verify parent\'s name details as required on Form 49A/49AA.',
      'Our operator reviews your documents and prepares the submission on the official NSDL/UTIITSL portal.',
      'Physical card delivery is handled directly by India Post to your registered address.'
    ],
    checklist: [
      'Proof of Identity (Aadhaar Card / Voter ID / Passport)',
      'Proof of Date of Birth (Birth Certificate / 10th Marksheet / Aadhaar)',
      'Proof of Address (Utility Bill / Bank Statement / Rent Agreement)',
      'Two Recent Passport Size Photographs',
      'Existing PAN Card Copy (Only required for corrections/updates)'
    ],
    applicationTypes: ['New PAN Card', 'Correction in Existing PAN', 'Reprint Lost PAN Card']
  },
  {
    id: 'voter_id',
    title: 'Voter ID Assistance',
    subtitle: 'New Registration & Electoral Roll Correction',
    category: 'Elections & Citizenship',
    feeInr: 149.0,
    turnaroundTime: '7 to 10 Working Days',
    officialPortal: 'Election Commission of India (ECI / Voters Portal)',
    description: 'Complete assistance for new voter registration (Form 6), shifting of residence / correction of entries (Form 8), and linking Aadhaar (Form 6B) through the official Voter Portal.',
    instructions: [
      'Applicant must be an Indian citizen and at least 18 years of age.',
      'Select your correct Assembly Constituency based on current residential address.',
      'Ensure family member EPIC number is available for reference (optional but helpful).',
      'BLO (Booth Level Officer) physical verification may be initiated by the election authority.'
    ],
    checklist: [
      'Proof of Age (Birth Certificate / Aadhaar / School Certificate)',
      'Proof of Ordinary Residence (Electricity Bill / Water Bill / Gas Connection)',
      'Recent Passport Size Color Photograph',
      'Existing EPIC / Voter ID (For correction/shifting)'
    ],
    applicationTypes: ['New Registration (Form 6)', 'Correction / Shifting (Form 8)', 'Duplicate Voter Card']
  },
  {
    id: 'aadhaar_guidance',
    title: 'Aadhaar Service Guidance',
    subtitle: 'Official UIDAI Appointment & Update Assistance',
    category: 'UIDAI Official Facilitation',
    feeInr: 99.0,
    turnaroundTime: 'Instant Guidance & Slot Booking',
    officialPortal: 'Unique Identification Authority of India (UIDAI)',
    description: 'Official procedure advisory, document checklist verification, and slot booking assistance at nearest official Aadhaar Seva Kendra (ASK) for biometric and demographic updates.',
    instructions: [
      'Notice: As per UIDAI guidelines, biometric capture (fingerprints/iris) requires physical presence at an official UIDAI ASK.',
      'TT Online Services assists with checklist verification, document preparation, and online appointment booking.',
      'We never collect your Aadhaar OTP or biometric data.',
      'Carry original documents during your scheduled Seva Kendra visit.'
    ],
    checklist: [
      'Valid Identity Proof (from UIDAI approved list)',
      'Valid Address Proof (Passport / Bank Passbook / Electricity Bill)',
      'Proof of Date of Birth (Birth Certificate / SSLC Book)',
      'Existing Aadhaar Slip or 12-digit UID number'
    ],
    applicationTypes: ['Seva Kendra Appointment Booking', 'Address Update Guidance', 'Child (Baal) Aadhaar Guidance', 'Biometric Update Guidance']
  },
  {
    id: 'driving_licence',
    title: 'Driving Licence Assistance',
    subtitle: 'Learner\'s & Permanent DL Application',
    category: 'Transport & Parivahan',
    feeInr: 299.0,
    turnaroundTime: '5 to 7 Working Days',
    officialPortal: 'Ministry of Road Transport (Sarathi Parivahan)',
    description: 'Hassle-free facilitation for Learner\'s Licence (LL), Permanent Driving Licence (DL) test slot booking, DL renewal, and address change on the Sarathi portal.',
    instructions: [
      'Select appropriate vehicle category (MCWOG, MCWG, LMV).',
      'Self-declaration Form 1 is filled online; Form 1A medical certificate required for applicants above 40 years.',
      'Learner\'s Licence test slot or contactless online LL test guidance provided.',
      'Permanent DL requires practical driving test at your designated RTO.'
    ],
    checklist: [
      'Age Proof (10th Marksheet / Birth Certificate / Aadhaar)',
      'Present Address Proof (Ration Card / Aadhaar / Passport)',
      'Medical Certificate Form 1A (if aged 40+ or for transport vehicles)',
      'Valid Learner\'s Licence Copy (for Permanent DL application)'
    ],
    applicationTypes: ['New Learner Licence (LL)', 'Permanent Driving Licence (DL)', 'Driving Licence Renewal', 'Address Change in DL']
  },
  {
    id: 'passport',
    title: 'Passport Application Assistance',
    subtitle: 'Fresh & Renewal Seva Kendra Booking',
    category: 'External Affairs & Travel',
    feeInr: 499.0,
    turnaroundTime: '2 to 4 Working Days',
    officialPortal: 'Passport Seva Portal, Ministry of External Affairs',
    description: 'End-to-end guidance for Fresh Indian Ordinary Passport, Re-issue of expired passport, minor passport, and Passport Seva Kendra (PSK/POPSK) appointment slot selection.',
    instructions: [
      'Verify that spelling of name and parents\' names match all educational and identity certificates.',
      'Non-ECR (Emigration Check Not Required) eligibility requires matriculation or higher qualification certificate.',
      'Appointments are booked based on official MEA slot release times.',
      'Police verification follows your PSK document verification.'
    ],
    checklist: [
      'Proof of Date of Birth (Birth Certificate / Matriculation Certificate)',
      'Proof of Present Address (Aadhaar / Bank Passbook / Electricity Bill)',
      'Educational Qualification Certificate (for Non-ECR status)',
      'Old Passport Booklet (all 36/60 pages if applying for Re-issue)'
    ],
    applicationTypes: ['Fresh Passport (Normal)', 'Re-issue / Expired Passport', 'Tatkaal Advisory & Booking', 'Minor Passport']
  },
  {
    id: 'birth_certificate',
    title: 'Birth Certificate Assistance',
    subtitle: 'Registration & Municipal Record Assistance',
    category: 'Civil Registration (CRS)',
    feeInr: 249.0,
    turnaroundTime: '7 to 14 Working Days',
    officialPortal: 'Civil Registration System (CRS) & State Urban/Rural Portals',
    description: 'Expert facilitation for applying for Birth Certificate issuance, delayed birth registration advisory under Section 13, and municipal ward corrections.',
    instructions: [
      'Births occurred in hospitals are typically reported via institutional discharge summary.',
      'For delayed registrations (beyond 21 days), SDM/Executive Magistrate order or affidavit may be needed.',
      'Double check spelling of child\'s name and parents\' names before submission.'
    ],
    checklist: [
      'Hospital Birth Slip / Discharge Summary',
      'Parents\' Identity Proofs (Aadhaar / Voter ID / Passport)',
      'Parents\' Marriage Certificate (if available)',
      'Affidavit for delayed registration (if birth is older than 1 year)'
    ],
    applicationTypes: ['New Birth Certificate Issuance', 'Delayed Birth Registration', 'Name Inclusion / Correction']
  },
  {
    id: 'income_certificate',
    title: 'Income Certificate Assistance',
    subtitle: 'Revenue Department State e-District Portal',
    category: 'Revenue & Scholarships',
    feeInr: 199.0,
    turnaroundTime: '5 to 10 Working Days',
    officialPortal: 'State e-District & Revenue Department',
    description: 'Assistance for obtaining state government certified Income Certificate for scholarship applications, fee concessions, government welfare schemes, and EWS quota.',
    instructions: [
      'Income declared must reflect annual income of the entire household.',
      'Attach recent salary slip or IT return if employed in organized sector.',
      'For agriculture/unorganized workers, village Talathi/Patwari report or self-declaration affidavit is required.'
    ],
    checklist: [
      'Income Proof (Salary Certificate / Form 16 / ITR / Patwari Report)',
      'Identity Proof (Aadhaar Card / Voter ID)',
      'Residential Proof (Ration Card / Electricity Bill)',
      'Income Affidavit signed before Executive Magistrate / Notary'
    ],
    applicationTypes: ['Fresh Income Certificate', 'Renewal of Income Certificate', 'Income Certificate for Scholarship']
  },
  {
    id: 'caste_certificate',
    title: 'Caste Certificate Assistance',
    subtitle: 'SC, ST, OBC, SEBC & EWS Verification',
    category: 'Social Justice & Welfare',
    feeInr: 249.0,
    turnaroundTime: '10 to 15 Working Days',
    officialPortal: 'State e-District & Backward Classes Welfare Dept',
    description: 'Comprehensive advisory and application preparation for SC, ST, OBC, VJNT, and EWS caste certificates, including lineage document verification.',
    instructions: [
      'Father\'s or paternal blood relative\'s caste certificate is crucial proof.',
      'School leaving certificate (LC/TC) showing recorded caste/religion must be provided.',
      'Non-Creamy Layer (NCL) certificate requires consecutive 3 years\' income proof for OBC candidates.'
    ],
    checklist: [
      'Applicant\'s School Leaving Certificate / Bonafide',
      'Father\'s / Paternal Relative\'s Caste Certificate',
      'Ancestral Residence Proof in State prior to cutoff year',
      'Affidavit in prescribed government format'
    ],
    applicationTypes: ['OBC Certificate', 'SC / ST Certificate', 'EWS Certificate', 'Non-Creamy Layer (NCL)']
  },
  {
    id: 'domicile_cert',
    title: 'Domicile Certificate Assistance',
    subtitle: 'State Residency & Domicile Proof',
    category: 'Revenue & State Administration',
    feeInr: 199.0,
    turnaroundTime: '7 to 12 Working Days',
    officialPortal: 'State Revenue Department & Citizen Portal',
    description: 'Professional assistance for obtaining State Domicile and Residence Certificate required for state government admissions, civil exams, and state quotas.',
    instructions: [
      'Applicant or parents must satisfy minimum state continuous residency requirement (usually 10 to 15 years).',
      'School records across primary, secondary, and higher secondary serve as primary continuous residency proof.',
      'Electricity bills or property tax receipts over the required years can corroborate stay.'
    ],
    checklist: [
      'Continuous Residency Proof (School certificates, Ration card, Rent agreements)',
      'Applicant & Parents\' Identity Proof (Aadhaar / Voter ID)',
      'Electricity Bill / Property Tax Receipt of residence',
      'Residence Affidavit affirmed before Notary'
    ],
    applicationTypes: ['State Domicile Certificate', 'Resident Certificate', 'Both Domicile and Nationality']
  }
];
