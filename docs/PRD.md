# VocaMoney — Voice-first AI Expense Tracker

> **Implementation-ready PRD & Engineering Brief for Claude**  
> Version: 1.0 · Date: 2026-11-23 · Language: Vietnamese · Target: Android-first MVP  
> Status: **Proposed specification, not a validated implementation**

---

## 0. INSTRUCTIONS FOR CLAUDE (READ FIRST)

You are acting as a senior Product Engineer, Android Engineer, Backend Engineer, AI Engineer, and QA Engineer building **VocaMoney**, a Vietnamese-language **voice-first personal expense tracker**.

### Non-negotiable rules

1. **Voice is the primary interface**. The user explicitly taps a microphone control before recording; never implement always-on, background, or covert microphone recording.
2. **Bank notification access is optional**. The product must work for cash and manual voice entries with bank access disabled/unavailable. Never request banking credentials or OTP.
3. **Do not invent money or transactions**. Never hallucinate missing amounts, silently change bank transaction amounts, or double-count linked bank/voice records.
4. **Do not treat LLM output as trusted facts**. Validate structured outputs, types, amounts, intents, categories, dates, and bank matches before saving.
5. **Privacy by default**: local-first storage; no raw audio retention by default; opt-in cloud processing and sync; data deletion/export controls.
6. **No automatic popups or mic activation following a bank notification**. Use an actionable notification that the user taps to open an activity; respect background-activity and microphone restrictions.
7. **Do not assert support for specific banks** until formats have been tested. Bank capture is an adapter-based optional enhancement.
8. **Keep unknown/ambiguous cases pending review**, and allow users to correct anything before committing.
9. **Use Vietnamese for product copy**, English names for code identifiers. Currency is VND and user-local timezone defaults to `Asia/Ho_Chi_Minh` unless device preference says otherwise.
10. **Implement vertically, test each slice**, do not just scaffold empty screens. Do not pretend APIs, permissions, integrations, or tests work when not implemented.
11. Before coding, inspect the existing repository. If it already has conventions, prefer minimal compatible changes. If no repository exists, create the proposed structure in §10.
12. After each milestone report: files changed, how to run, tests performed, known limitations, security impact, and next steps.

### What to deliver

- A functional **Android MVP** in Kotlin + Jetpack Compose (recommended).
- On-device transactional data store, voice capture, review and confirmation UI, ledger and dashboard.
- Pluggable speech recognition and intent parsing; fully functional **deterministic fake implementations** for development and tests, with clear production integration points.
- Optional bank notification ingestion with an explicit permission flow, mock adapter and review/transaction matching.
- Unit tests, relevant instrumentation/UI tests, sample anonymized data, basic documentation and run instructions.
- Optional backend **only if required** for cloud AI. Core voice-to-manual-entry flow should be testable without a running backend.

### Proposed engineering decisions (revise only with explanation)

| Area | MVP default |
|---|---|
| Platform | Android Native, Kotlin, Jetpack Compose, Material 3 |
| Architecture | MVVM + use-cases + repositories, unidirectional UI state |
| Local persistence | Room / SQLite |
| Background jobs | WorkManager where justified, not for permanent mic listening |
| Voice | Push-to-talk; Android SpeechRecognizer adapter or another explicitly configured ASR provider |
| AI parser | Validated JSON schema + deterministic mock + configurable real provider |
| Data | Offline-first, avoid cloud dependency for initial ledger |
| Bank capture | Optional Android NotificationListenerService behind explicit user opt-in |
| Time | UTC instants in storage, local timezone only in display and relative-date parsing |
| Monetary amounts | Integer minor units for VND; never Float/Double for money |
| App copy | Vietnamese |
| Visual | Clean fintech: primary `#137C69`, navy `#112B3C`, mint `#E7F5EF`, accent `#F6BC61` |

---

## 1. PRODUCT VISION

**Positioning:** *"Nói một câu. Ghi một khoản. Hiểu mọi đồng tiền."*

A user taps the mic and naturally says:

- “Mua cà phê 35 nghìn.” → Create draft expense: 35,000 VND, `FOOD_COFFEE`.
- “Sáng nay mua bánh mì 25 nghìn và cà phê 35 nghìn.” → Draft **two** expenses of 25,000 and 35,000 VND; total 60,000 VND.
- “Đổ xăng xe máy 50 nghìn tiền mặt.” → Draft cash expense: 50,000 VND, `TRANSPORT_FUEL`.
- “85 nghìn vừa chuyển khoản là tiền ăn trưa.” → Attempt to **link** an existing pending bank transaction, not create an additional expense.
- “Nhận lương 15 triệu.” → Draft income, not an expense.

Core value: capture transactions quickly without opening a complex form, while maintaining high accuracy and user control.

### Goals

1. One tap to start voice input, clear results, minimal confirmation friction.
2. Native Vietnamese amounts, natural language, relative dates, and multiple expenses in one utterance.
3. Independent from banking integrations; cash must work from day one.
4. Reliable source linkage and no double-counting.
5. Transparent, reversible AI operations.

### Suggested beta targets — hypotheses, not measured results

- P90 time for a single simple expense: **≤ 8 seconds**, evaluated on supported devices and network conditions.
- Top-level category accuracy: **≥ 90%** on curated labeled Vietnamese test samples.
- Voice session completion without manual form: **≥ 80%**.
- Day-30 retention: **≥ 25%** (product experiment, not a promise).
- Duplicate ledger creation from retrying one voice session: **0** in deterministic concurrency tests.

### Non-goals for MVP

No payment initiation; no transfers; no bank credential or OTP access; no always-on hotword listening; no investment advice; no complex personal wealth forecasts; no assumption of iOS bank-notification access; no automatic correction of a source bank amount.

---

## 2. PERSONAS & JOBS TO BE DONE

| Persona | Job |
|---|---|
| Office worker | Quickly log coffee, meals, transport and QR purchases |
| Family budget manager | Log groceries, tuition, bills, cash and mixed purchases |
| QR-heavy user | Describe a bank debit without retyping amount |
| Cash-heavy user | Log expenses without bank integrations |

**Main job:** “When I have just spent money, help me log the reason in seconds with my voice and keep my records accurate.”

---

## 3. SCOPE / PRIORITIES

### P0 — required for MVP

- Onboarding and privacy disclosures; microphone permission just-in-time.
- Home with prominent microphone action.
- Voice idle/listening/processing/preview/error/saved states.
- Vietnamese transcription via swappable ASR adapter; keyboard fallback.
- Parse amount, intent, description, category, optional date and payment method.
- Handle one and multiple items per utterance.
- Preview and edit each extracted item; confirm/save or cancel.
- Offline local ledger, categories, history, transaction details, edit/delete with audit event.
- Basic dashboard of confirmed income/expenses by day/month/category.
- Optional bank notification settings and parsing adapter where feasible, with a safe mock.
- Bank transaction inbox and candidate linking, conflict warnings, duplicate protection.
- Explicit empty/loading/error states and accessibility basics.

### P1 — after core proves reliable

- Home-screen widget / launcher shortcut to voice.
- Smart reminders and notification grouping, under user control.
- Voice inquiries about recorded spending.
- User-defined merchant rules and AI learning from corrected category assignments.
- User-configurable budgets, spending alerts.
- Data export and optional encrypted cloud sync (consider export as P0 if required by compliance/release review).

### P2

- Receipt OCR, richer trend analysis, advanced recurring expense insights, iOS version with alternative transaction ingestion.

---

## 4. COMPLETE USER FLOWS

### UF-01 — Voice-only single expense

1. Open Home → tap central mic.
2. If needed, show why microphone permission is needed → request permission.
3. Display `LISTENING` and clear stop/cancel controls; record/transcribe after explicit consent.
4. User: “Mua cà phê 35 nghìn.”
5. ASR returns transcript; intent parser extracts `CREATE_EXPENSE`, amount 35,000 VND, category coffee.
6. Validator checks positive amount, supported currency, known category, plausible date.
7. Show preview with editable description, amount, category, time/payment method.
8. User confirms → commit one `ledger_entry` atomically → success screen with Undo.
9. User may tap “Ghi thêm” to immediately start another session.

**Error:** missing amount → inline clarification “Khoản này bao nhiêu tiền?”; do not invent an amount. ASR error → retry or type.

### UF-02 — Multi-item utterance

1. User: “Sáng nay mua bánh mì 25 nghìn và cà phê 35 nghìn.”
2. Parser creates a **draft batch** of two expense items.
3. Preview displays both rows and computed total 60,000 VND.
4. User may edit/remove each item; total must recalculate deterministically.
5. Save all items in **one database transaction**, with a common `voice_session_id` and `batch_id`.
6. Retry with same idempotency key must not create duplicates.

### UF-03 — Bank QR + voice category

1. User pays 85,000 VND by QR in banking app.
2. If user opted in and the source is supported, Android receives a bank notification.
3. Normalize and validate notification; persist `bank_transaction` with `PENDING_REVIEW` when classification is not clear.
4. Show a regular **actionable notification**: “85.000đ vừa thanh toán. Bạn đã mua gì?” with a user-tappable action “Nói mục đích”.
5. User taps → opens voice screen tied to a specific bank transaction ID.
6. User: “Tiền ăn trưa.” Amount comes from **known bank transaction context**, not from an LLM guess.
7. Preview shows 85,000 VND and Lunch; user confirms.
8. Create/update ledger allocation linked to bank transaction; ensure **one economic transaction is counted once**.
9. If amount conflicts or match is ambiguous, require user choice/confirmation.

### UF-04 — Cash voice expense

User: “Đổ xăng xe máy 50 nghìn tiền mặt” → no bank link → show 50,000 VND, Fuel, Cash → confirm → ledger.

### UF-05 — Income

User: “Nhận lương 15 triệu” → classify `INCOME`; add to income reporting but not expenses.

### UF-06 — Modify prior entry

User: “Sửa khoản cà phê sáng thành 55 nghìn.” → `EDIT_TRANSACTION`; search authorized user-owned entries by time/category/description. If multiple match, show choices. On selected item, show old → new values and request confirmation. Record audit event.

### UF-07 — Failed speech / permission denied / offline

- Permission denied: no recording; offer typed entry and a path to settings.
- Noisy/incomplete ASR: show transcript, request correction, no silent save.
- Offline: local ledger and typed entry work. If recognition/parser need network, present an explicit unavailable state; do not claim offline ASR if unsupported on that device.
- App killed during processing: recover a saved draft safely where possible; never double-post.

### UF-08 — Pending bank entries

Show an inbox with all unmatched source transactions. User can speak, choose category by touch, mark as transfer/internal, ignore, or review later. Never pressure user with repeated notifications.

---

## 5. SCREEN INVENTORY & UX REQUIREMENTS

### Style tokens

- Primary `#137C69`; deep navy `#112B3C`; mint `#E7F5EF`; accent `#F6BC61`; surface `#F5F7F8`.
- Prefer Material 3, accessible contrast, 8dp spacing system, 16–20dp card radii.
- Typography: Vietnamese-capable sans-serif (e.g., system font or Be Vietnam Pro if licensed/available).
- Main bottom navigation: **Tổng quan / Lịch sử / Ghi voice / Cài đặt**. AI insights can live under a P1 route rather than a fake-functional tab.
- Button labels and microcopy in Vietnamese. Distinguish **“Đã ghi nhận”** from **“Đang chờ xác nhận”**.

| ID | Screen | Must include |
|---|---|---|
| S01 | Welcome | Value proposition, no misleading bank claims |
| S02 | Voice onboarding | One realistic spoken example |
| S03 | Permissions | Mic just-in-time; optional bank notification access separately |
| S04 | Home | Prominent mic; totals; pending bank inbox preview |
| S05 | Voice capture | Idle, listening indicator, stop, cancel, accessibility labels |
| S06 | AI preview | Transcript; itemized editable amounts/categories; total; source/match; confirm |
| S07 | Entry detail | Original source, edited fields, audit trail summary, delete |
| S08 | History | Date/category/type filters, empty states, entry list |
| S09 | Bank inbox | Pending entries; selected context; voice action, mark reviewed |
| S10 | Categories/settings | Permission status, privacy, notification preferences, categories |
| S11 | Dashboard | Confirmed income/expenses; monthly/category summaries, exclusions |
| S12 | AI insights | P1 only; do not ship a non-functional AI conversation as MVP |

### Voice screen states

`IDLE → LISTENING → PROCESSING → PREVIEW → SAVING → SAVED`;

Errors branch to `PERMISSION_DENIED`, `ASR_UNAVAILABLE`, `ASR_TIMEOUT`, `ASR_ERROR`, `PARSER_ERROR`, `VALIDATION_NEEDED`, `SAVE_FAILED`. Each should offer a recovery action.

### Usability

- Minimum accessible tap target 48dp for controls where possible.
- Clear focus order for TalkBack, content descriptions, and no color-only state signals.
- Recording notification/indicator and explicit cancel; never start recording just because screen opened from a notification.
- No blocking forced voice input; typed correction always available.
- User preview always shows **currency** and **transaction type**.

---

## 6. FUNCTIONAL REQUIREMENTS

### Epic A — voice

| ID | Requirement | Acceptance condition |
|---|---|---|
| VOI-01 | Tap mic to start | Permission request at first use; mic does not activate pre-tap |
| VOI-02 | Listening feedback | Visual indicator, Stop and Cancel |
| VOI-03 | ASR Vietnamese | Transcript displayed and editable |
| VOI-04 | Amount speech normalization | `35k`, `35 nghìn`, `ba mươi lăm ngàn` → 35000 |
| VOI-05 | Multiple items | Creates distinct previews, correct total |
| VOI-06 | Missing amount | Blocks silent save; clarifies |
| VOI-07 | Typed fallback | Works without microphone permission |
| VOI-08 | Cancel | No ledger write after cancellation |
| VOI-09 | Immediate repeat | “Ghi thêm” returns to ready state |
| VOI-10 | Privacy | No raw audio storage by default |

### Epic B — intent parser

| ID | Intent | Example | Behavior |
|---|---|---|---|
| INT-01 | CREATE_EXPENSE | Mua sữa 120 nghìn | Draft expense |
| INT-02 | CREATE_INCOME | Nhận lương 15 triệu | Draft income |
| INT-03 | MULTI_EXPENSE | Rau 30k, thịt 90k | Draft multiple expenses |
| INT-04 | LINK_TRANSACTION | Khoản 85k là ăn trưa | Suggest a bank match |
| INT-05 | EDIT_TRANSACTION | Sửa cà phê sáng thành 55k | Resolve existing entry, then confirm |
| INT-06 | UNKNOWN | Hôm nay tiêu kha khá | Request specific detail |

### Epic C — banking (optional, Android)

| ID | Requirement | Acceptance condition |
|---|---|---|
| BNK-01 | Explicit bank source opt-in | Disabled until user enables; clear explanation |
| BNK-02 | Allowlisted package/source adapters | Never parse unrelated app notifications as financial events |
| BNK-03 | Parse directional transaction | Inflow/outflow/unknown distinct |
| BNK-04 | Reject irrelevant messages | Ignore OTP, ads, unsuccessful transfers |
| BNK-05 | De-duplicate | Duplicate notification ≠ duplicate transaction |
| BNK-06 | Voice matching | Exact known context preferred; ambiguity requires user choice |
| BNK-07 | Amount conflict | Flag discrepancy, bank source amount immutable as imported |
| BNK-08 | User controls | Disable source, view pending items, no repeated harassment |

### Epic D — ledger/dashboard

| ID | Requirement | Acceptance condition |
|---|---|---|
| LED-01 | Transactional save | Multi-item batch saves all or none |
| LED-02 | Idempotent commands | Repeated request with same idempotency key writes once |
| LED-03 | Edit/delete | Audit event, dashboard updates |
| LED-04 | Reports | Only eligible, confirmed economic entries included; no double-counting |
| LED-05 | Internal transfer | Excluded from expense aggregate |
| LED-06 | Source traceability | User can see voice/cash/bank provenance |

---

## 7. BUSINESS RULES & DATA INTEGRITY

| Rule | Description |
|---|---|
| BR-01 | Audio recording only after deliberate user action and platform permission |
| BR-02 | No money amount is invented if neither speech nor trusted context supplies it |
| BR-03 | An LLM cannot silently override a source bank amount |
| BR-04 | Re-processing a `voice_session_id` with same idempotency key must not duplicate entries |
| BR-05 | One imported bank transfer can support multiple category allocations, but allocations must reconcile and cannot inflate overall expense |
| BR-06 | Internal transfer between accounts user owns is not consumption spending |
| BR-07 | When two candidate bank transfers match, user must choose explicitly |
| BR-08 | Editing an old transaction requires an unambiguous target and explicit confirmation |
| BR-09 | Unknown and partial inputs remain drafts/pending, not final ledger entries |
| BR-10 | User corrections win over automatic categorization until explicitly changed |
| BR-11 | All bank inputs are untrusted external strings; never execute content embedded in notification |
| BR-12 | Keep time as UTC instants; resolve “hôm qua”, “sáng nay” in user timezone |
| BR-13 | Negative amounts, unsupported currency, overflow and malformed numbers fail validation |
| BR-14 | Removing a source notification permission does not silently delete confirmed user ledger entries |
| BR-15 | Deleted records follow explicit retention/recovery policy; users can review effects on totals |

### Reporting invariant

- **Ledger is the financial source of truth**, not individual notification counts.
- A pending bank transaction, an unconfirmed draft, and a duplicate alert are **not** confirmed expenses.
- An imported bank transaction with N allocations must be counted according to the **single economic debit**, not as debit + N extra expenses.
- If allocating across multiple items, require allocation sum = source transaction amount, or show a clear unallocated difference before confirmation.

---

## 8. DATA MODEL (PROPOSED)

Recommended Room entities / equivalent SQL structures:

### `voice_sessions`

`id (UUID PK)`, `created_at_utc`, `updated_at_utc`, `status`, `transcript_nullable`, `recognizer_provider`, `error_code_nullable`, `linked_bank_transaction_id_nullable`, `idempotency_key (UNIQUE)`, `retention_expires_at_nullable`.

> Do not persist raw audio by default. If cloud ASR is used, show opt-in and limit transmission/retention.

### `bank_transactions`

`id (UUID PK)`, `source_package`, `source_reference_nullable`, `direction`, `amount_vnd_long_nullable`, `occurred_at_utc_nullable`, `counterparty_nullable`, `status`, `fingerprint`, `created_at_utc`, `updated_at_utc`.

Bank `status` is independent from ledger status. Imported raw text should preferably be discarded after extraction or kept only with a minimal, explicitly defined short retention period.

### `ledger_entries`

`id (UUID PK)`, `batch_id_nullable`, `voice_session_id_nullable`, `type (EXPENSE|INCOME|TRANSFER)`, `amount_vnd_long`, `category_id_nullable`, `description`, `note_nullable`, `payment_method (CASH|BANK|OTHER|UNKNOWN)`, `occurred_at_utc`, `status (DRAFT|CONFIRMED|EXCLUDED|DELETED)`, `source (VOICE|BANK|MANUAL)`, `created_at_utc`, `updated_at_utc`.

### `bank_entry_allocations`

`id`, `bank_transaction_id FK`, `ledger_entry_id FK`, `allocated_amount_vnd_long`; unique constraint on `(bank_transaction_id, ledger_entry_id)` and transactional reconciliation.

### Supporting tables

- `categories`: `id`, `parent_id`, `name_vi`, `type`, `is_system`, `sort_order`.
- `user_rules`: recognized merchant/category mappings, priority, enabled flag, timestamps.
- `audit_events`: entity reference, action, old/new safe fields, actor type, timestamp.
- `app_preferences`: key/value or typed DataStore; bank source choices and cloud opt-in.
- `processed_notifications`: minimal dedup identifiers with bounded retention.

### Category taxonomy seed

- `FOOD_BREAKFAST` — Ăn uống / Bữa sáng
- `FOOD_COFFEE` — Ăn uống / Cà phê
- `FOOD_LUNCH` — Ăn uống / Bữa trưa
- `FOOD_GROCERIES` — Ăn uống / Đi chợ
- `TRANSPORT_FUEL` — Đi lại / Xăng xe
- `TRANSPORT_OTHER` — Đi lại / Khác
- `EDUCATION_TUITION` — Giáo dục / Học phí
- `UTILITIES_ELECTRICITY` — Hóa đơn / Tiền điện
- `SHOPPING_OTHER` — Mua sắm / Khác
- `INCOME_SALARY` — Thu nhập / Tiền lương
- `UNCATEGORIZED` — Chưa phân loại

Categories are editable/extendable later; stable IDs are preferred over free-form LLM category names.

---

## 9. AI CONTRACTS & VALIDATION

### Parse request (internal interface)

```kotlin
interface IntentParser {
    suspend fun parse(
        transcript: String,
        context: ParseContext
    ): ParseResult
}
```

Context may include locale, timezone, current timestamp, approved category IDs and **only the relevant known bank transaction context**. Avoid sending unrelated account history.

### Example validated payload

```json
{
  "schema_version": "1.0",
  "intent": "MULTI_EXPENSE",
  "language": "vi-VN",
  "items": [
    {
      "description": "Bánh mì",
      "amount_vnd": 25000,
      "category_id": "FOOD_BREAKFAST",
      "payment_method": "UNKNOWN"
    },
    {
      "description": "Cà phê",
      "amount_vnd": 35000,
      "category_id": "FOOD_COFFEE",
      "payment_method": "UNKNOWN"
    }
  ],
  "requires_confirmation": true,
  "unresolved_fields": []
}
```

**Example input:** “Sáng nay mua bánh mì 25 nghìn và cà phê 35 nghìn.”  
**Important:** `occurred_at_utc` must be derived from the request context and relative-date resolver, not hardcoded by AI.

### Post-parser validation sequence

1. Validate JSON schema, enums, item count cap, length limits, required fields.
2. Parse integer amount safely; check `> 0`, within `Long` range and configured safeguards.
3. Map category to a known user/system ID; unknown → `UNCATEGORIZED` + review.
4. Resolve relative dates using timezone-aware deterministic utilities.
5. Validate intent/entry type consistency (salary must be income).
6. Apply bank candidate matching only within authorized account/user scope.
7. Check idempotency and pending conflicting allocations.
8. Return `DraftPreview` or `NeedClarification`; do not write ledger automatically.

### Suggested mock parsing fixture table

| Transcript | Expected |
|---|---|
| `Mua cà phê 35k` | expense, 35000, FOOD_COFFEE |
| `Mua rau 30k và thịt 90k` | 2 items, 30000+90000 |
| `Đổ xăng năm mươi nghìn` | expense, 50000, TRANSPORT_FUEL |
| `Nhận lương 15 triệu` | income, 15000000, INCOME_SALARY |
| `Mua cà phê` | missing amount → clarification unless bank context supplies one |
| `85 nghìn vừa chuyển khoản là ăn trưa` | LINK_TRANSACTION; suggest matching bank item |
| `Sửa cà phê sáng thành 55 nghìn` | EDIT_TRANSACTION; confirmation required |
| `Hôm nay tiêu kha khá` | UNKNOWN or clarification; no ledger write |

### AI safety / reliability

- Treat transcript and notifications as **data**, never instructions to run code, leak secrets or delete accounts.
- Limit token use, payload length, output tokens, network retries and timeouts.
- No PII/financial text in analytics or crash logs.
- Prefer a deterministic rule-based amount parser before invoking LLM where possible.
- Measure entity/amount accuracy separately from category accuracy.
- A numeric “confidence” produced by an LLM is **not** a calibrated probability; do not base silent-save decisions solely on it.

---

## 10. REPOSITORY STRUCTURE (FOR A NEW REPOSITORY)

```text
vocamoney/
├── README.md
├── docs/
│   ├── PRD.md
│   ├── ARCHITECTURE.md
│   ├── PRIVACY.md
│   └── TEST_PLAN.md
├── android/
│   ├── app/
│   │   └── src/
│   │       ├── main/
│   │       │   ├── java/.../vocamoney/
│   │       │   │   ├── core/            # design system, navigation, utilities
│   │       │   │   ├── data/            # Room, repositories, ASR/parser adapters
│   │       │   │   ├── domain/          # entities, use-cases, validators
│   │       │   │   ├── feature/home/
│   │       │   │   ├── feature/voice/
│   │       │   │   ├── feature/preview/
│   │       │   │   ├── feature/history/
│   │       │   │   ├── feature/bankinbox/
│   │       │   │   ├── feature/dashboard/
│   │       │   │   └── feature/settings/
│   │       │   └── AndroidManifest.xml
│   │       ├── test/
│   │       └── androidTest/
│   └── build.gradle.kts
├── backend/                   # only when cloud parser is actually used
│   ├── README.md
│   └── ...
└── fixtures/
    ├── utterances_vi.json
    └── bank_notifications_mock.json
```

Do not expose the LLM API key inside the Android APK. If using a paid cloud provider, build a minimal authenticated backend proxy with per-user limits and data minimization. For local mock mode, no backend is required.

---

## 11. API / DOMAIN COMMAND CONTRACTS

These are suggested **internal use-cases**, not a requirement to ship a remote API:

```text
StartVoiceSession()
StopVoiceSession(sessionId)
CancelVoiceSession(sessionId)
TranscribeVoice(sessionId) -> Transcript | Error
ParseIntent(transcript, optionalBankContext) -> DraftBatch | NeedClarification
EditDraftItem(batchId, itemId, patch)
ConfirmDraftBatch(batchId, idempotencyKey) -> SavedEntryIds
ListLedgerEntries(filters)
GetLedgerEntry(entryId)
UpdateLedgerEntry(entryId, patch, expectedVersion)
DeleteLedgerEntry(entryId, expectedVersion)
IngestBankNotification(source, notification) -> Parsed|Ignored|Pending
FindBankCandidates(draft, context) -> Candidates
LinkLedgerToBank(entryIds, bankId, allocations, expectedVersion)
GetDashboard(period) -> Aggregates
```

For a cloud-backed AI parser only, an optional contract:

`POST /api/v1/voice/parse` → request `{session_id, transcript, locale, timezone, bank_context?}`; response is a validated draft or clarification. Use auth, rate limits, schema version, redaction, and request idempotency. Do **not** send raw audio to this route unless the provider and consent model explicitly require it.

### Data and error states

- Validation failure → typed error with user-correctable fields.
- Provider timeout → retry option without re-creating entries.
- No bank candidate → draft standalone or request explicit source; never fake an association.
- Multiple candidates → selection UI; no auto-link.
- Network unavailable → local draft remains available; clear offline messaging.

---

## 12. ANDROID PLATFORM CONSTRAINTS / SECURITY CHECKLIST

**Verify the current official Android and Google Play documentation before implementation and publication.** API behavior may vary by Android version, device and OEM.

1. Request `RECORD_AUDIO` only when user starts voice; respect microphone privacy toggles and lifecycle.
2. `SpeechRecognizer` availability and on-device recognition vary by device/services; handle missing services and offline cases explicitly.
3. `NotificationListenerService` requires explicit user-level access in system settings and should be optional.
4. Avoid launching Activity automatically from background notifications; show notification actions that open UI following a user interaction.
5. On supported Android versions, request notification posting permission when necessary and explain consequences of denial.
6. Do not access SMS, screen contents, accessibility services or OTP for bank collection.
7. Use Android Keystore for sensitive encryption keys; avoid unencrypted export/backups of financial records.
8. Do not hardcode tokens/secrets; redact logs; treat notification payloads as untrusted.
9. Provide a privacy policy, clear disclosure, user revocation/deletion flow, and legal review for applicable Vietnamese data protection obligations.
10. Test service restart, process death, battery optimization, reboot, source permission removal and bank-app notification format changes.
11. Never claim a bank integration works until it passes device-level fixture and real opt-in tests.

Privacy requirement: prioritize data minimization, consent, right to delete/export, access isolation, secure storage. Legal compliance must be reviewed using official current sources before production.

---

## 13. QA / ACCEPTANCE TEST MATRIX

| Test ID | Scenario | Expected result |
|---|---|---|
| TC-01 | `Mua cà phê 35k` | Draft 35000, FOOD_COFFEE |
| TC-02 | `Mua rau 30k, thịt 90k` | Two entries, total 120000 |
| TC-03 | `Đổ xăng năm mươi nghìn tiền mặt` | Cash expense, 50000 |
| TC-04 | `Nhận lương 15 triệu` | Income 15000000 |
| TC-05 | `Mua cà phê` no trusted amount | Clarification, zero ledger writes |
| TC-06 | Retry same voice session after timeout | One committed batch only |
| TC-07 | Linked bank 85000 + voice 85000 | One economic expense counted |
| TC-08 | Bank 85000 vs voice 95000 | Conflict UI, no silent source overwrite |
| TC-09 | `Hôm qua mua sách 100k` | Yesterday in device/user timezone |
| TC-10 | ASR fails/noise | Retry or typed correction; no accidental write |
| TC-11 | Micro permission denied/revoked | Typed fallback; no crash |
| TC-12 | Cancel while recording | No ledger write |
| TC-13 | Overflow/negative/unparseable amount | Validation error |
| TC-14 | Two same-amount bank candidates | Explicit selection required |
| TC-15 | Edit references two coffee entries | Disambiguation list |
| TC-16 | Offline mode | Local ledger usable; network-only voice steps transparently unavailable |
| TC-17 | Identical amount twice at different times | Do not falsely deduplicate distinct purchases |
| TC-18 | Duplicate notification delivered N times | At most one source transaction record |
| TC-19 | Multiple draft items: one invalid | Batch cannot partially save without explicit user correction |
| TC-20 | User deletes edited expense | Audit/delete policy applied; totals refresh |
| TC-21 | Voice `1 triệu rưỡi` | Normalize correctly to 1500000 or ask if ambiguous |
| TC-22 | Unicode and regional variants (`ngàn`, `nghìn`, `k`) | Correct parsing or explicit clarification |
| TC-23 | Two requests save same batch concurrently | Transaction/idempotency prevents duplicate commits |
| TC-24 | Malicious transcript prompt injection | Treated as text, never system command |
| TC-25 | Import inflow, OTP, marketing notification | No consumption expense created |
| TC-26 | Bank notification incomplete/ambiguous | Pending review, no invented financial fields |
| TC-27 | User denies notification access | Voice-only mode fully functional |
| TC-28 | Reopen process after kill | No duplicate; safe draft recovery if implemented |

### Testing levels

- Unit: amount parser, dates, category mapping, validators, dedupe, allocation reconciliation, dashboard aggregation.
- Integration: Room transactions; mock ASR → parser → draft → ledger; optional notification adapter.
- UI: permission denied, success, voice error, preview editing, bank conflict, accessibility labels.
- Security: secret scanning, log leakage, malicious notification strings, data isolation, backup settings.
- Pilot: opt-in Vietnamese speech samples with varied accents/noise; report word/entity accuracy and user correction rate.

---

## 14. ITERATIVE DELIVERY PLAN

These are **planning assumptions**, not guaranteed timelines.

| Phase | Goal | Definition of done |
|---|---|---|
| Sprint 1 (Weeks 1–2) | Core repo, design system, fake voice, Room, deterministic parser | Single cash voice fixture from mic tap → preview → saved ledger works end-to-end |
| Sprint 2 (Weeks 3–4) | Real ASR adapter, multi-expense, validation, edit, history | Real Vietnamese speech with review path; unit tests green |
| Sprint 3 (Weeks 5–6) | Optional bank capture + matching, dashboard | QR source can be linked once; no duplicate totals; permission-denied fallback |
| Sprint 4 (Weeks 7–8) | QA, accessibility, hardening, closed beta | Critical tests pass; known platform limitations documented; privacy review checklist complete |

**Suggested team:** 1 Product/UX, 1–2 Android developers, 1 backend/AI developer (part-time or full-time depending on cloud integration), 1 QA. Duration depends on bank coverage, ASR provider and distribution review.

### Recommended initial development order

1. Build functional UI on fake seeded voice and ledger data.
2. Add verified amount/date parsing and validation.
3. Add Room storage with atomic writes and idempotency.
4. Add Android microphone and ASR adapter; keep typed/fake modes for reliable tests.
5. Add transaction bank inbox with mock events before real listener integration.
6. Add a single real bank source adapter only after format tests and permission review.
7. Add privacy controls and release-quality QA.

---

## 15. RELEASE GATES

### Gate 1 — Technical POC

- Can ASR capture Vietnamese money phrases on target devices?
- Can the optional bank listener detect enough information without misreading non-transaction messages?
- Do current Android / Play policies permit the planned permissions/disclosures/distribution?

### Gate 2 — Functional MVP

- Full voice-to-ledger flow works with no bank permissions.
- Offline manual/typed fallback works.
- No incorrect automatic amount synthesis.
- Bank and voice events do not double-count.
- Critical tests passing; app never stores raw mic audio by default.

### Gate 3 — Closed beta

- Run with a consenting sample of ~50–100 testers (proposal, not yet recruited).
- Measure time-to-record, ASR quality, parsing precision, correction frequency, task completion, crashes, reliability and trust.
- Prioritize defects tied to incorrect financial records.

### Gate 4 — Go/No-go

- Product, privacy, platform and reliability gates approved.
- Explicitly document any unsupported bank sources / Android versions / offline-ASR modes.

---

## 16. CLAUDE WORK PLAN / FIRST RESPONSE EXPECTATION

When you receive this file, **do not stop at summarizing the PRD**. Start implementing the smallest complete vertical slice in the repository.

**Step 1:** Inspect repository, identify build system and version constraints.  
**Step 2:** Produce a short architecture plan, dependency list, project tree, and first milestone acceptance checklist.  
**Step 3:** Implement:

- Home with prominent tap-to-talk button.
- Fake ASR/intent parser with the exact Vietnamese fixtures in §9.
- Draft preview supporting single and multiple expense items.
- Validation of positive integer VND amounts.
- Room ledger with one atomic confirm action and idempotency.
- History and basic totals that update on save.
- Unit tests covering at least TC-01 through TC-06, TC-12, TC-19, TC-23.

**Step 4:** Explain precisely how to run/build/test it, what is mocked, and how to add real ASR next.

### Implementation report template

```markdown
## Completed
- ...

## Files changed
- ...

## Commands run / results
- ...

## Acceptance checks
- [x] ...
- [ ] ...

## Known limitations / unverified assumptions
- ...

## Next vertical slice
- ...
```

### Final acceptance principle

**If the user can speak “Mua cà phê 35 nghìn”, see a correct editable preview, save it exactly once, and view it in history — without any bank connection — the product has a working Voice-first core.**

---

_End of implementation brief._
