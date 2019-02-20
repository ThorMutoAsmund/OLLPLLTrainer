package org.nafai.ollplltrainer;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by thora_000 on 04/01/2018.
 */


public class Cube33 {
    private static final String TAG = "Cube33";

    // Faces
    private final int FU = 0, FF = 1, FL = 2, FR = 3, FB = 4, FD = 5;
    // Operations
    private final int NOP = 0;
    private final int U = 1, F = 2, L = 3, R = 4, B = 5, D = 6;
    private final int U_ = 7, F_ = 8, L_ = 9, R_ = 10, B_ = 11, D_ = 12;
    private final int M = 13, E = 14, S = 15, M_ = 16, E_ = 17, S_ = 18;

    private static final int DIM = 3, FACES = 6, CPF = 9; // cubies per face

    private int[][] p;

    public Cube33() {
        p = new int[FACES][CPF];
        init();
    }

    public void init() {
        for (int f = 0; f < FACES; ++f) {
            for (int c = 0; c < CPF; ++c) {
                p[f][c] = f;
            }
        }
    }

    private String Yellow(int c) {
        return c == 0 ? "0" : ".";
    }

    public int getLLAsInt() {
        int a = p[FU][0] == 0 ? 1 : (p[FB][6] == 0 ? 2 : (p[FL][2] == 0 ? 3 : 0));
        int b = p[FU][1] == 0 ? 1 : (p[FB][7] == 0 ? 2 : 0);
        int c = p[FU][2] == 0 ? 1 : (p[FR][8] == 0 ? 2 : (p[FB][8] == 0 ? 3 : 0));
        int d = p[FU][3] == 0 ? 1 : (p[FL][5] == 0 ? 2 : 0);
        int e = p[FU][4] == 0 ? 1 : 0;
        int f = p[FU][5] == 0 ? 1 : (p[FR][5] == 0 ? 2 : 0);
        int g = p[FU][6] == 0 ? 1 : (p[FL][8] == 0 ? 2 : (p[FF][0] == 0 ? 3 : 0));
        int h = p[FU][7] == 0 ? 1 : (p[FF][1] == 0 ? 2 : 0);
        int i = p[FU][8] == 0 ? 1 : (p[FF][2] == 0 ? 2 : (p[FR][2] == 0 ? 3 : 0));

        int r1 = a + (b<<2) + (c<<4) + (d<<6) + (e<<8) + (f<<10) + (g<<12) + (h<<14) + (i<<16);
        int r2 = c + (f<<2) + (i<<4) + (b<<6) + (e<<8) + (h<<10) + (a<<12) + (d<<14) + (g<<16);
        int r3 = i + (h<<2) + (g<<4) + (f<<6) + (e<<8) + (d<<10) + (c<<12) + (b<<14) + (a<<16);
        int r4 = g + (d<<2) + (a<<4) + (h<<6) + (e<<8) + (b<<10) + (i<<12) + (f<<14) + (c<<16);

        return Math.min(Math.min(r1,r2),Math.min(r3,r4));
    }

    public void debugWhetherSolved() {
        Log.d(TAG, isSolved() ? "It is solved!" : "It is NOT solved");
    }

    public void debugWhetherLLOriented() {
        Log.d(TAG, isLLOriented() ? "LL is correctly oriented!" : "LL is NOT correctly oriented");
    }

    public boolean perform(String alg) {
        return perform(alg, false, false);
    }

    public boolean performReverse(String alg) {
        return perform(alg, false, true);
    }

    public boolean performDebug(String alg) {
        return perform(alg, true, false);
    }

    public boolean perform(String alg, boolean debug, boolean reverse) {
        // should check for illegal algs
        boolean isBuffering = false;
        int repeat = 1;
        char op = 0;
        boolean opReverse = false;
        boolean opWide = false;
        ArrayList<String> ops = new ArrayList<String>();
        ArrayList<String> buffer = new ArrayList<String>();
        for (char c : (alg+"#").toCharArray()) {
            if (c == ' ' || c == 9) {
                continue;
            }
            else if (c == '(') {
                if (isBuffering) {
                    return false;
                }
                isBuffering = true;
                continue;
            }
            else if (c == ')') {
                if (!isBuffering) {
                    return false;
                }
                isBuffering = false;
            }
            else if (c == '\'') {
                if (op == 0 || opReverse) {
                    return false;
                }
                opReverse = true;
                continue;
            }
            else if (c == 'w' || c == 'W') {
                if (op == 0 || opWide ||
                        (op != 'U' && op != 'F' && op != 'L' && op != 'R' && op != 'B' && op != 'D' )) {
                    return false;
                }
                opWide = true;
                continue;
            }
            else if (c >= '1' && c <= '9') {
                if (op == 0 && buffer.size() == 0) {
                    return false;
                }
                repeat = c - '0';
                continue;
            }

            if (c == '#' || c == ')' || c == 'u' || c == 'f' || c == 'l' || c == 'r' || c == 'b' || c == 'd' ||
                    c == 'U' || c == 'F' || c == 'L' || c == 'R' || c == 'B' || c == 'D' ||
                    c == 'x' || c == 'y' || c == 'z' || c == 'M' || c == 'E' || c == 'S') {

                if (op > 0) {
                    // Create string op
                    String opToAdd = Character.toString(op) + ( opWide ? "w" : "") + (opReverse ? (!reverse ? "'" : "") : (!reverse ? "" : "'"));

                    // Add op to buffer
                    for (int i=0; i<repeat; ++i) {
                        buffer.add(opToAdd);
                    }
                    repeat = 1;
                }


                // If there is no new op, just continue
                if (c == ')') {
                    op = 0;
                    opWide = false;
                    opReverse = false;
                    repeat = 1;
                    continue;
                }

                // Add to final ops list
                for (int i=0; i<repeat; ++i) {
                    for (String b  : buffer) {
                        ops.add(b);
                    }
                }
                buffer.clear();

                op = c;

                // Reset op
                opWide = false;
                opReverse = false;
                repeat = 1;

                if (op == '#') {
                    break;
                }
            }
            else {
                return false;
            }
        }

        if (reverse) {
            Collections.reverse(ops);
        }
        for (String o : ops) {
            if (debug) {
                Log.d(TAG, "Performing " + o);
            }
            performOp(parse(o));
        }

        return true;
    }

    public boolean isSolved() {
        for (int f = 0; f < FACES; ++f) {
            for (int c = 0; c < CPF; ++c) {
                if (p[f][c] != f) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean isLLOriented() {
        for (int c = 0; c < CPF; ++c) {
            if (p[0][c] != 0) {
                return false;
            }
        }

        return true;
    }

    public void debug() {
        Log.d(TAG, "-----------");
        Log.d(TAG, "    " + p[FB][0] + p[FB][1] + p[FB][2]);
        Log.d(TAG, "    " + p[FB][3] + p[FB][4] + p[FB][5]);
        Log.d(TAG, "    " + p[FB][6] + p[FB][7] + p[FB][8]);
        Log.d(TAG, "" + p[FL][0] + p[FL][1] + p[FL][2] + " " + p[FU][0] + p[FU][1] + p[FU][2] + " " + p[FR][8] + p[FR][7] + p[FR][6]);
        Log.d(TAG, "" + p[FL][3] + p[FL][4] + p[FL][5] + " " + p[FU][3] + p[FU][4] + p[FU][5] + " " + p[FR][5] + p[FR][4] + p[FR][3]);
        Log.d(TAG, "" + p[FL][6] + p[FL][7] + p[FL][8] + " " + p[FU][6] + p[FU][7] + p[FU][8] + " " + p[FR][2] + p[FR][1] + p[FR][0]);
        Log.d(TAG, "    " + p[FF][0] + p[FF][1] + p[FF][2]);
        Log.d(TAG, "    " + p[FF][3] + p[FF][4] + p[FF][5]);
        Log.d(TAG, "    " + p[FF][6] + p[FF][7] + p[FF][8]);
        Log.d(TAG, "    " + p[FD][0] + p[FD][1] + p[FD][2]);
        Log.d(TAG, "    " + p[FD][3] + p[FD][4] + p[FD][5]);
        Log.d(TAG, "    " + p[FD][6] + p[FD][7] + p[FD][8]);
    }

    public String faceName(int f) {
        switch (f) {
            case FU: return "U";
            case FF: return "F";
            case FL: return "L";
            case FR: return "R";
            case FB: return "B";
            case FD: return "D";
        }
        return "";
    }

    private int parse(String symbol) {
        switch (symbol.trim()) {
            case "U": return U;
            case "F": return F;
            case "L": return L;
            case "R": return R;
            case "B": return B;
            case "D": return D;
            case "U'": return U_;
            case "F'": return F_;
            case "L'": return L_;
            case "R'": return R_;
            case "B'": return B_;
            case "D'": return D_;
            case "M": return M;
            case "E": return E;
            case "S": return S;
            case "M'": return M_;
            case "E'": return E_;
            case "S'": return S_;
            case "u": case "Uw": return U + (E_ << 8);
            case "f": case "Fw": return F + (S << 8);
            case "l": case "Lw": return L + (M << 8);
            case "r": case "Rw":return R + (M_ << 8);
            case "b": case "Bw": return B + (S_ << 8);
            case "d": case "Dw": return D + (E << 8);
            case "u'": case "Uw'": return U_ + (E << 8);
            case "f'": case "Fw'": return F_ + (S_ << 8);
            case "l'": case "Lw'": return L_ + (M_ << 8);
            case "r'": case "Rw'": return R_ + (M << 8);
            case "b'": case "Bw'": return B_ + (S << 8);
            case "d'": case "Dw'": return D_ + (E_ << 8);
            case "x": return R + (M_ << 8) + (L_ << 16);
            case "y": return U + (E_ << 8) + (D_ << 16);
            case "z": return F + (S << 8) + (B_ << 16);
            case "x'": return L + (M << 8) + (R_ << 16);
            case "y'": return D + (E << 8) + (U_ << 16);
            case "z'": return B + (S_ << 8) + (F_ << 16);
        }
        return 0;
    }

    private void performOp(int ops) {
        while (ops > 0) {
            int op = ops & 0xff;
            switch(op) {
                case U: case U_: performOp(op == U, new int[][][] {
                        {{FF,0,1,2}, {FL,2,5,8}, {FB,8,7,6}, {FR,2,5,8}},
                        {{FU,0,3,6}, {FU,2,1,0}, {FU,8,5,2}, {FU,6,7,8}}}); break;
                case F: case F_: performOp(op == F, new int[][][] {
                        {{FR,0,1,2}, {FD,0,1,2}, {FL,8,7,6}, {FU,8,7,6}},
                        {{FF,0,3,6}, {FF,2,1,0}, {FF,8,5,2}, {FF,6,7,8}}}); break;
                case L: case L_: performOp(op == L, new int[][][] {
                        {{FU,0,3,6}, {FF,0,3,6}, {FD,0,3,6}, {FB,0,3,6}},
                        {{FL,0,3,6}, {FL,2,1,0}, {FL,8,5,2}, {FL,6,7,8}}}); break;
                case R: case R_: performOp(op == R, new int[][][] {
                        {{FU,8,5,2}, {FB,8,5,2}, {FD,8,5,2}, {FF,8,5,2}},
                        {{FR,0,3,6}, {FR,2,1,0}, {FR,8,5,2}, {FR,6,7,8}}}); break;
                case B: case B_: performOp(op == B, new int[][][] {
                        {{FR,6,7,8}, {FU,2,1,0}, {FL,2,1,0}, {FD,6,7,8}},
                        {{FB,0,3,6}, {FB,2,1,0}, {FB,8,5,2}, {FB,6,7,8}}}); break;
                case D: case D_: performOp(op == D, new int[][][] {
                        {{FF,6,7,8}, {FR,0,3,6}, {FB,2,1,0}, {FL,0,3,6}},
                        {{FD,0,3,6}, {FD,2,1,0}, {FD,8,5,2}, {FD,6,7,8}}}); break;
                case M: case M_: performOp(op == M, new int[][][] {
                        {{FU,1,4,7}, {FF,1,4,7}, {FD,1,4,7}, {FB,1,4,7}}}); break;
                case E: case E_: performOp(op == E, new int[][][] {
                        {{FF,3,4,5}, {FR,1,4,7}, {FB,5,4,3}, {FL,1,4,7}}}); break;
                case S: case S_: performOp(op == S, new int[][][] {
                        {{FU,3,4,5}, {FR,5,4,3}, {FD,5,4,3}, {FL,3,4,5}}}); break;
            }

            ops >>= 8;
        }
    }

    private boolean performOp(boolean forward, int[][][] seriesList) {
        int[][] clone = new int[FACES][];
        for (int[][] series : seriesList) {
            for(int f = 0 ; f < FACES; ++f) {
                clone[f] = (int[])p[f].clone();
            }
            // backup last slot
            int[] row = series[forward ? series.length-1 : 0];
            int face = row[0];

            int limit = (forward ? -1 : series.length);
            for (int r = forward ? series.length-2 : 1; true == (forward ? r >= limit : r <= limit); r+=(forward ? -1 : 1)) {
                int[] nextRow = series[(r+series.length) % series.length];
                int nextFace = nextRow[0];
                for (int i=0; i<DIM; ++i) {
                    p[face][row[i+1]] = clone[nextFace][nextRow[i+1]];
                }
                row = nextRow;
                face = nextFace;
            }
        }

        return true;
    }
}
