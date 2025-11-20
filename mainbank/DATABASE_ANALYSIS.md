# Database Analysis Report
**Generated:** 2025-11-20  
**Database:** H2 Database v2.1.214  
**Location:** ./data/banking.mv.db

---

## Executive Summary

The banking system database contains **3 main tables** with the following data:
- **8 Users** (5 Admins, 3 Regular Users)
- **12 Customers**
- **11 Accounts**

### ⚠️ CRITICAL ISSUES IDENTIFIED

1. **Schema Mismatch**: The database schema does not match the application code
2. **Missing Columns**: `CUSTOMERS` table is missing required columns (`USER_ID`, `FIRST_NAME`, `SURNAME`, `ADDRESS`)
3. **Missing Columns**: `ACCOUNTS` table is missing `BRANCH` and `DATE_OPENED` columns
4. **No Foreign Key**: `CUSTOMERS.USER_ID` foreign key to `USERS.ID` is not present

---

## Current Database Schema

### USERS Table
```
Columns:
  - USER_NAME (CHARACTER VARYING) - Legacy column
  - IS_ADMIN (BOOLEAN) - Legacy column
  - REMARKS (CHARACTER VARYING) - Legacy column
  - ID (BIGINT) - Primary Key ✓
  - USERNAME (CHARACTER VARYING) - ✓
  - PASSWORD (CHARACTER VARYING) - ✓
  - EMAIL (CHARACTER VARYING) - ✓
  - ROLE (CHARACTER VARYING) - ✓

Status: ⚠️ Contains legacy columns that should be removed
```

### CUSTOMERS Table
```
Columns:
  - ID (BIGINT) - Primary Key ✓
  - NAME (CHARACTER VARYING) - ⚠️ Should be split into FIRST_NAME and SURNAME
  - EMAIL (CHARACTER VARYING) - ✓
  - PHONE (CHARACTER VARYING) - ✓

Missing Columns:
  ❌ USER_ID (BIGINT) - Foreign key to USERS.ID
  ❌ FIRST_NAME (VARCHAR) - Required by application
  ❌ SURNAME (VARCHAR) - Required by application
  ❌ ADDRESS (VARCHAR) - Required by application

Status: ❌ CRITICAL - Schema does not match application code
```

### ACCOUNTS Table
```
Columns:
  - ID (BIGINT) - Primary Key ✓
  - ACCOUNT_NUMBER (CHARACTER VARYING) - ✓
  - CUSTOMER_ID (BIGINT) - Foreign Key to CUSTOMERS.ID ✓
  - ACCOUNT_TYPE (CHARACTER VARYING) - ✓
  - BALANCE (NUMERIC) - ✓
  - INTEREST_RATE (NUMERIC) - ✓

Missing Columns:
  ❌ BRANCH (VARCHAR) - Required by Account model
  ❌ DATE_OPENED (TIMESTAMP) - Required by Account model

Status: ⚠️ Missing optional columns
```

---

## Expected Schema (From Code)

### USERS Table (Expected)
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    role VARCHAR(20) DEFAULT 'USER'
)
```

### CUSTOMERS Table (Expected)
```sql
CREATE TABLE customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    first_name VARCHAR(100) NOT NULL,
    surname VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(20),
    email VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
)
```

### ACCOUNTS Table (Expected)
```sql
CREATE TABLE accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    customer_id BIGINT NOT NULL,
    account_type VARCHAR(20) NOT NULL,
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    interest_rate DECIMAL(5,4),
    branch VARCHAR(100),
    date_opened TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
)
```

---

## Data Statistics

### Users
- **Total:** 8 users
- **Admins:** 5
- **Regular Users:** 3
- **Status:** ✓ Data present

### Customers
- **Total:** 12 customers
- **Sample Data:**
  - ID: 1, Name: "lt", Email: "lt@", Phone: "65445"
  - ID: 2, Name: "John Doe", Email: "john@email.com", Phone: "555-0101"
  - ID: 3, Name: "Jane Smith", Email: "jane@email.com", Phone: "555-0102"
- **Status:** ⚠️ Data exists but schema is incompatible with application

### Accounts
- **Total:** 11 accounts
- **Status:** ⚠️ Data exists but missing branch and date information

---

## Relationship Analysis

### Current Relationships
```
USERS (1) ----< (?) CUSTOMERS (1) ----< (*) ACCOUNTS
          ❌ Missing FK        ✓ FK exists
```

### Issues:
1. **No User-Customer Link**: The `user_id` column doesn't exist in CUSTOMERS table
2. **Cannot track which user owns which customer profile**
3. **Cascade deletes won't work for User → Customer relationship**

---

## Impact Assessment

### High Priority Issues
1. **Application will fail** when trying to:
   - Save customers with `user_id`
   - Query customers by `first_name` and `surname`
   - Load accounts with `branch` information
   - Use any User → Customer relationship features

2. **Data Integrity**:
   - Cannot enforce referential integrity between Users and Customers
   - Risk of orphaned customer records

### Medium Priority Issues
1. Legacy columns in USERS table taking up space
2. Missing audit trail (date_opened) for accounts

---

## Recommended Actions

### 1. Database Migration (CRITICAL)
Execute the following migration script to update the schema:

```sql
-- Backup existing data first!

-- Step 1: Add new columns to CUSTOMERS
ALTER TABLE CUSTOMERS ADD COLUMN user_id BIGINT;
ALTER TABLE CUSTOMERS ADD COLUMN first_name VARCHAR(100);
ALTER TABLE CUSTOMERS ADD COLUMN surname VARCHAR(100);
ALTER TABLE CUSTOMERS ADD COLUMN address VARCHAR(255);

-- Step 2: Migrate data from NAME to FIRST_NAME and SURNAME
UPDATE CUSTOMERS SET 
    first_name = SUBSTRING(name, 1, LOCATE(' ', name) - 1),
    surname = SUBSTRING(name, LOCATE(' ', name) + 1)
WHERE LOCATE(' ', name) > 0;

UPDATE CUSTOMERS SET 
    first_name = name,
    surname = ''
WHERE LOCATE(' ', name) = 0;

-- Step 3: Add foreign key constraint
ALTER TABLE CUSTOMERS ADD CONSTRAINT fk_customer_user 
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- Step 4: Add columns to ACCOUNTS
ALTER TABLE ACCOUNTS ADD COLUMN branch VARCHAR(100) DEFAULT 'Main Branch';
ALTER TABLE ACCOUNTS ADD COLUMN date_opened TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Step 5: Clean up USERS table (optional)
ALTER TABLE USERS DROP COLUMN user_name;
ALTER TABLE USERS DROP COLUMN is_admin;
ALTER TABLE USERS DROP COLUMN remarks;

-- Step 6: Drop old NAME column from CUSTOMERS (after verification)
-- ALTER TABLE CUSTOMERS DROP COLUMN name;
```

### 2. Data Validation
After migration:
- Verify all customers have first_name and surname
- Check that all foreign key relationships are valid
- Ensure no data was lost during migration

### 3. Application Testing
- Test customer creation with user linkage
- Test account operations with branch information
- Verify cascade deletes work correctly

---

## Database Health Checklist

- [ ] Schema matches application code
- [ ] All foreign keys are defined
- [ ] Cascade delete rules are in place
- [ ] No orphaned records exist
- [ ] All required columns are present
- [ ] Data types are appropriate
- [ ] Indexes are optimized
- [ ] Backup strategy is in place

**Current Status:** ❌ 2/8 checks passed

---

## Conclusion

The database requires **immediate migration** to align with the application code. The current schema mismatch will cause runtime errors when:
- Creating or updating customers
- Linking users to customers
- Loading account details with branch information

**Recommendation:** Execute the migration script in a test environment first, then apply to production after verification.

---

## Files Affected

- `DatabaseManager.java` - Contains expected schema
- `CustomerDAO.java` - Expects user_id, first_name, surname, address
- `AccountDAO.java` - Expects branch, date_opened
- All GUI components that interact with customers and accounts

---

**Report Generated By:** Database Analyzer Utility  
**Next Review:** After schema migration
