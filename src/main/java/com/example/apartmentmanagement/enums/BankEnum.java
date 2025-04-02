package com.example.apartmentmanagement.enums;

public enum BankEnum {
    AGRIBANK("Ngân hàng Nông nghiệp và Phát triển Nông thôn Việt Nam", "970405"),
    VIETCOMBANK("Ngân hàng Ngoại thương Việt Nam", "970436"),
    VIETINBANK("Ngân hàng Công thương Việt Nam", "970415"),
    MBBANK("Ngân hàng Quân đội", "970422"),
    TECHCOMBANK("Ngân hàng Kỹ thương", "970407"),
    SACOMBANK("Ngân hàng Sài Gòn Thương Tín", "970403"),
    VPBANK("Ngân hàng Việt Nam Thịnh Vượng", "970432"),
    TPBANK("Ngân hàng Tiên Phong", "970423"),
    BIDV("Ngân hàng Đầu tư và Phát triển Việt Nam", "970418"),
    ACB("Ngân hàng Á Châu", "970416"),
    HSBC("Ngân hàng HSBC", "970437"),
    STANDARD_CHARTERED("Ngân hàng Standard Chartered", "970423");

    private final String fullName;
    private final String bin;

    BankEnum(String fullName, String bin) {
        this.fullName = fullName;
        this.bin = bin;
    }

    public String getFullName() {
        return fullName;
    }

    public String getBin() {
        return bin;
    }

    // Phương thức tìm kiếm ngân hàng theo tên
    public static BankEnum findByName(String name) {
        for (BankEnum bank : values()) {
            if (bank.name().equalsIgnoreCase(name) ||
                    bank.fullName.toLowerCase().contains(name.toLowerCase())) {
                return bank;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy ngân hàng: " + name);
    }

    // Phương thức tìm kiếm ngân hàng theo BIN
    public static BankEnum findByBin(String bin) {
        for (BankEnum bank : values()) {
            if (bank.bin.equals(bin)) {
                return bank;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy ngân hàng với BIN: " + bin);
    }
}