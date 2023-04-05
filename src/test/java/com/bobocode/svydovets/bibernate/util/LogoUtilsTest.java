package com.bobocode.svydovets.bibernate.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class LogoUtilsTest {

    @Test
    void testGetBibernateLogo() {
        var expectedLogo = getExpectedBibernateLogo();

        var actualLogo = LogoUtils.getBibernateLogo();

        assertEquals(expectedLogo, actualLogo);
    }

    private String getExpectedBibernateLogo() {
        return "\n\r .----------------. .----------------. .----------------. .----------------. .----------------."
                + " .-----------------..----------------. .----------------. .----------------. \n\r"
                + "| .--------------. | .--------------. | .--------------. | .--------------. | .-----------"
                + "---. | .--------------. | .--------------. | .--------------. | .--------------. |\n\r"
                + "| |   ______     | | |     _____    | | |   ______     | | |  _________   | | |  _______    "
                + " | | | ____  _____  | | |      __      | | |  _________   | | |  _________   | |\n\r"
                + "| |  |_   _ \\    | | |    |_   _|   | | |  |_   _ \\    | | | |_   ___  |  | | | |_   __ \\   "
                + " | | ||_   \\|_   _| | | |     /  \\     | | | |  _   _  |  | | | |_   ___  |  | |\n\r"
                + "| |    | |_) |   | | |      | |     | | |    | |_) |   | | |   | |_  \\_|  | | |   | |__) |   | "
                + "| |  |   \\ | |   | | |    / /\\ \\    | | | |_/ | | \\_|  | | |   | |_  \\_|  | |\n\r"
                + "| |    |  __'.   | | |      | |     | | |    |  __'.   | | |   |  _|  _   | | |   |  __ /    | |"
                + " |  | |\\ \\| |   | | |   / ____ \\   | | |     | |      | | |   |  _|  _   | |\n\r"
                + "| |   _| |__) |  | | |     _| |_    | | |   _| |__) |  | | |  _| |___/ |  | | |  _| |  \\ \\_  |"
                + " | | _| |_\\   |_  | | | _/ /    \\ \\_ | | |    _| |_     | | |  _| |___/ |  | |\n\r"
                + "| |  |_______/   | | |    |_____|   | | |  |_______/   | | | |_________|  | | | |____| |___| | |"
                + " ||_____|\\____| | | ||____|  |____|| | |   |_____|    | | | |_________|  | |\n\r"
                + "| |              | | |              | | |              | | |              | | |              | | "
                + "|              | | |              | | |              | | |              | |\n\r"
                + "| '--------------' | '--------------' | '--------------' | '--------------' | '--------------' | "
                + "'--------------' | '--------------' | '--------------' | '--------------' |\n\r"
                + " '----------------' '----------------' '----------------' '----------------' '----------------' '"
                + "----------------' '----------------' '----------------' '----------------' \n\r";
    }
}
