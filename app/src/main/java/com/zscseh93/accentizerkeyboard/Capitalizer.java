package com.zscseh93.accentizerkeyboard;

public class Capitalizer {

    public String capitalize(String word, String mask) {

        StringBuilder capitalizedWord = new StringBuilder();
        for (int i = 0; i < mask.length(); i++) {
            if (Character.isUpperCase(mask.charAt(i))) {
                capitalizedWord.append(Character.toUpperCase(word.charAt(i)));
            } else {
                capitalizedWord.append(word.charAt(i));
            }
        }

        return capitalizedWord.toString();
    }
}
