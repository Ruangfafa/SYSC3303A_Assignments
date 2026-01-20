package common;

public final class Constants {
    public static final class ApplicationCon {
        public static final String
                L_START = "Application starting...",
                L_OVER = "All threads started.",

                ASSEMBLER = "Assembler",
                TECH_FRAME = "Technician-FRAME",
                TECH_PROP = "Technician-PROPULSION",
                TECH_FIRM =  "Technician-FIRMWARE";
        private ApplicationCon() {}
    }
    public static final class AssemblyTableCon {
        public static final String
            L_PLACED = "Components placed on table: %s, %s",
            L_ASSEMBLED = "[%s]Drone assembled by %s | Total = %s";
        private AssemblyTableCon() {}
    }
    public static final class TechnicianCon {
        public static final String
            L_START = "Technician started: %s",
            L_EXIT = "[%s] Technician exiting.";
    }
    public static final class AssemblerCon {
        public static final String
            L_START = "Assembler started.",
            L_EXIT = "Assembly complete. Assembler exiting.";
    }
    private Constants() {
    }
}
