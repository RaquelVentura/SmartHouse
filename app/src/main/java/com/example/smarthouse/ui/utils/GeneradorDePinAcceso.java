package com.example.smarthouse.ui.utils;

import java.security.SecureRandom;

public class GeneradorDePinAcceso {
    public static String generarPin() {
        SecureRandom random = new SecureRandom();
        String letras = "0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(letras.charAt(random.nextInt(letras.length())));
        }
        return sb.toString();
    }
}
