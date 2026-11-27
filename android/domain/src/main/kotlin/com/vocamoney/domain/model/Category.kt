package com.vocamoney.domain.model

/** A spending or income category. Stable string IDs, never free-form LLM names (PRD §8). */
data class Category(
    val id: String,
    val parentNameVi: String,
    val nameVi: String,
    val type: CategoryType,
    val sortOrder: Int,
    val isSystem: Boolean = true,
)

/** System categories seeded into a fresh database (PRD §8, "Category taxonomy seed"). */
object CategorySeed {
    const val UNCATEGORIZED = "UNCATEGORIZED"

    val all: List<Category> = listOf(
        Category("FOOD_BREAKFAST", "Ăn uống", "Bữa sáng", CategoryType.EXPENSE, 10),
        Category("FOOD_COFFEE", "Ăn uống", "Cà phê", CategoryType.EXPENSE, 20),
        Category("FOOD_LUNCH", "Ăn uống", "Bữa trưa", CategoryType.EXPENSE, 30),
        Category("FOOD_GROCERIES", "Ăn uống", "Đi chợ", CategoryType.EXPENSE, 40),
        Category("TRANSPORT_FUEL", "Đi lại", "Xăng xe", CategoryType.EXPENSE, 50),
        Category("TRANSPORT_OTHER", "Đi lại", "Khác", CategoryType.EXPENSE, 60),
        Category("EDUCATION_TUITION", "Giáo dục", "Học phí", CategoryType.EXPENSE, 70),
        Category("UTILITIES_ELECTRICITY", "Hóa đơn", "Tiền điện", CategoryType.EXPENSE, 80),
        Category("SHOPPING_OTHER", "Mua sắm", "Khác", CategoryType.EXPENSE, 90),
        Category("INCOME_SALARY", "Thu nhập", "Tiền lương", CategoryType.INCOME, 100),
        Category(UNCATEGORIZED, "Chưa phân loại", "Chưa phân loại", CategoryType.ANY, 1000),
    )

    private val byId = all.associateBy { it.id }

    fun find(id: String): Category? = byId[id]
}
