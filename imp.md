Complete Build Plan
Phase 1 — Foundation Fixes (Do First, Everything Depends On This)
Task	Why
Refactor User.roles to Set<Role>	Multi-role support
Add CORS config	Frontend can call backend
Move JWT secret to application.yml	Security + config
Create PhoneOtp entity (mirror of EmailOtp)	Phone OTP flow
Add StorageService interface + LocalStorageServiceImpl	File uploads
Phase 2 — Retailer Onboarding Flow

/register/retailer page → POST /api/v1/retailer/register
     ↓
Phone OTP verification → POST /api/v1/otp/send-phone-otp
                       → POST /api/v1/otp/verify-phone-otp
     ↓
/portal/kyc page → POST /api/v1/kyc/submit (file upload)
     ↓
/portal/kyc-review → GET /api/v1/kyc/status
New entities: RetailerProfile, KycDocument

Phase 3 — Core Business Flow

Dashboard → GET /api/v1/dashboard/summary
     ↓
New Bill:
  Find Customer → GET /api/v1/customers/search
  Add Customer → POST /api/v1/customers
               → POST /api/v1/customers/{id}/send-otp
               → POST /api/v1/customers/{id}/verify-otp
     ↓
Create Invoice → POST /api/v1/invoices
     ↓
Create Warranty → POST /api/v1/warranties
     ↓
Review & Confirm → GET /api/v1/invoices/{id}
     ↓
Invoice List → GET /api/v1/invoices
             → GET /api/v1/warranties
New entities: Customer, Invoice, InvoiceItem, Warranty

Phase 4 — Management Features

POST   /api/v1/agents
GET    /api/v1/agents
POST   /api/v1/templates
GET    /api/v1/templates
New entities: Agent, Template

Summary: Entity Roadmap
Entity	Phase	Linked To
User (refactor)	1	—
PhoneOtp	1	User
RetailerProfile	2	User
KycDocument	2	User
Customer	3	RetailerProfile
Invoice	3	RetailerProfile, Customer
InvoiceItem	3	Invoice
Warranty	3	Invoice
Agent	4	RetailerProfile
Template	4	RetailerProfile
