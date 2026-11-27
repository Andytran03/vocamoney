package com.vocamoney.domain.model

/** Kind of ledger entry (PRD §8). A transfer between the user's own accounts is not spending (BR-06). */
enum class EntryType { EXPENSE, INCOME, TRANSFER }

/** Only CONFIRMED entries count toward totals; drafts never do (PRD §7, reporting invariant). */
enum class EntryStatus { DRAFT, CONFIRMED, EXCLUDED, DELETED }

enum class PaymentMethod { CASH, BANK, OTHER, UNKNOWN }

enum class EntrySource { VOICE, BANK, MANUAL }

/** Voice screen states (PRD §5). Error states each need a recovery action in the UI. */
enum class VoiceSessionStatus {
    IDLE,
    LISTENING,
    PROCESSING,
    PREVIEW,
    SAVING,
    SAVED,
    CANCELLED,
    PERMISSION_DENIED,
    ASR_UNAVAILABLE,
    ASR_TIMEOUT,
    ASR_ERROR,
    PARSER_ERROR,
    VALIDATION_NEEDED,
    SAVE_FAILED,
}

enum class CategoryType { EXPENSE, INCOME, ANY }
