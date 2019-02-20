package org.nafai.ollplltrainer;

/**
 * Created by thora_000 on 24/12/2017.
 */

public class EntryRotator {
    public static String rotate(String entry, int degrees) {
        String result = entry;
        if (degrees == 0) {
            return result;
        }

        if (entry.startsWith("y'") || entry.startsWith("(y')")) {
            entry =  entry.substring(
                    entry.startsWith("y'") ? 2 : 4
            ).trim();
            switch (degrees) {
                case 90:
                    return "(y2) " + entry;
                case 180:
                    return "(y) " + entry;
                case 270:
                    return entry;
            }
        }
        else if (entry.startsWith("y2") || entry.startsWith("(y2)")) {
            entry =  entry.substring(
                    entry.startsWith("y2") ? 2 : 4
            ).trim();
            switch (degrees) {
                case 90:
                    return "(y) " + entry;
                case 180:
                    return entry;
                case 270:
                    return "(y') " + entry;
            }
        }
        else if (entry.startsWith("y") || entry.startsWith("(y)")) {
            entry =  entry.substring(
                    entry.startsWith("y") ? 1 : 3
            ).trim();
            switch (degrees) {
                case 90:
                    return entry;
                case 180:
                    return "(y') " + entry;
                case 270:
                    return "(y2) " + entry;
            }
        }
        else  {
            switch (degrees) {
                case 90:
                    return "(y') " + entry;
                case 180:
                    return "(y2) " + entry;
                case 270:
                    return "(y) " + entry;
            }
        }

        return entry;

        /*
        // This code tried to change the algorithm depending on rotation, but in the end I opted to go with the above
        // solutions which just prepends (y) (y') or (y2)
        while (degrees > 0) {
            result = "";
            for (char ch : entry.toCharArray()) {
                switch (ch) {
                    case 'L':
                        result += "B";
                        break;
                    case 'B':
                        result += "R";
                        break;
                    case 'R':
                        result += "F";
                        break;
                    case 'F':
                        result += "L";
                        break;
                    case 'l':
                        result += "b";
                        break;
                    case 'b':
                        result += "r";
                        break;
                    case 'r':
                        result += "f";
                        break;
                    case 'f':
                        result += "l";
                        break;
                    case 'x':
                        result += "z";
                        break;
                    case 'z':
                        result += "x'";
                        break;
                    case 'M':
                        result += "S'";
                        break;
                    case 'S':
                        result += "M";
                        break;
                    default:
                        result += ch;
                }
            }
            result = result.replace("''","");
            entry = result;
            degrees -= 90;
        }
        return result;*/
    }
}
