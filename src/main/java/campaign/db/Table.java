package campaign.db;

public enum Table {
    ERROR_WARNING ("error_warning"), // Errors and warnings
    TEST_TYPE_USE ("testtype_use"), // Test types used
    SESSIONS ("sessions"), // Length of time spent in create/edit, q&a and test studying
    MULTIMEDIA_USE ("multimedia_use"), // Use of multi-media
    PROFESSOR ("Professor"),
    STUDENT("Student")
    ;

    private final String dbCode;

    Table(String str) {
        this.dbCode = str;
    }

    protected String getDbCode() {
        return this.dbCode;
    }

    protected String getDefaultErrorMsg() {
        return "No table set. Use setTable(Table.useAnEnum)";
    }
}
