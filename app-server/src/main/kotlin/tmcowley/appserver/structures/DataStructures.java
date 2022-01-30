package tmcowley.appserver.structures;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.checkerframework.checker.nullness.qual.NonNull;

import tmcowley.appserver.objects.Key;
import tmcowley.appserver.objects.KeyPair;

public class DataStructures {

    public static HashSet<String> getWordSet() {

        HashSet<String> allWords = new HashSet<String>();
        try {
            BufferedReader readIn = new BufferedReader(new FileReader("resources/words.txt"));
            String str;
            while ((str = readIn.readLine()) != null) {
                allWords.add(str);
            }
            readIn.close();
        } catch (IOException ex) {
            // ensure file: word-list.txt is in /app/resources/
            System.out.println(ex.getMessage());
            return null;
        }

        return allWords;
    }

    public static ArrayList<String> getWords(ArrayList<KeyPair> currentWord) {

        if (currentWord.isEmpty()) {
            return new ArrayList<String>();
        }

        // transform into tree
        WordTree currentWordTree = new WordTree();

        for (KeyPair keyPair : currentWord) {
            currentWordTree.insert(keyPair);
        }

        return currentWordTree.getWords();
    }

    public static ArrayList<String> getSentences(@NonNull ArrayList<ArrayList<String>> text) {

        // store in Binary Tree, traverse each as before
        SentenceTree sentenceTree = new SentenceTree();

        for (ArrayList<String> words : text) {
            sentenceTree.insert(words);
        }

        return sentenceTree.getWords();
    }

    public static HashMap<Key, KeyPair> getKeyPairHashMap() {

        // map keys
        final Key a = new Key('a');
        final Key b = new Key('b');
        final Key c = new Key('c');
        final Key d = new Key('d');
        final Key e = new Key('e');
        final Key f = new Key('f');
        final Key g = new Key('g');
        final Key h = new Key('h');
        final Key i = new Key('i');
        final Key j = new Key('j');
        final Key k = new Key('k');
        final Key l = new Key('l');
        final Key m = new Key('m');
        final Key n = new Key('n');
        final Key o = new Key('o');
        final Key p = new Key('p');
        final Key q = new Key('q');
        final Key r = new Key('r');
        final Key s = new Key('s');
        final Key t = new Key('t');
        final Key u = new Key('u');
        final Key v = new Key('v');
        final Key w = new Key('w');
        final Key x = new Key('x');
        final Key y = new Key('y');
        final Key z = new Key('z');

        // map key-pairs

        // top row
        final KeyPair qp = new KeyPair(q, p);
        final KeyPair wo = new KeyPair(w, o);
        final KeyPair ei = new KeyPair(e, i);
        final KeyPair ru = new KeyPair(r, u);
        final KeyPair ty = new KeyPair(t, y);

        // middle row
        // sort out dual chars w/ shift
        final KeyPair a_colon = new KeyPair(a, a);
        final KeyPair sl = new KeyPair(s, l);
        final KeyPair dk = new KeyPair(d, k);
        final KeyPair fj = new KeyPair(f, j);
        final KeyPair gh = new KeyPair(g, h);

        // bottom row
        final KeyPair z_f_slash = new KeyPair(z, z);
        final KeyPair x_dot = new KeyPair(x, x);
        final KeyPair c_comma = new KeyPair(c, c);
        final KeyPair vm = new KeyPair(v, m);
        final KeyPair bn = new KeyPair(b, n);

        // define key -> key-pair HM
        HashMap<Key, KeyPair> keyPairs = new HashMap<Key, KeyPair>(26);

        // top row
        {
            // top row: left half
            keyPairs.put(q, qp);
            keyPairs.put(w, wo);
            keyPairs.put(e, ei);
            keyPairs.put(r, ru);
            keyPairs.put(t, ty);

            // top row: right half
            keyPairs.put(p, qp);
            keyPairs.put(o, wo);
            keyPairs.put(i, ei);
            keyPairs.put(u, ru);
            keyPairs.put(y, ty);
        }

        // middle row
        {
            // middle row: left half
            keyPairs.put(a, a_colon);
            keyPairs.put(s, sl);
            keyPairs.put(d, dk);
            keyPairs.put(f, fj);
            keyPairs.put(g, gh);

            // middle row: right half
            keyPairs.put(a, a_colon);
            keyPairs.put(l, sl);
            keyPairs.put(k, dk);
            keyPairs.put(j, fj);
            keyPairs.put(h, gh);
        }

        // bottom row
        {
            // bottom row: left half
            keyPairs.put(z, z_f_slash);
            keyPairs.put(x, x_dot);
            keyPairs.put(c, c_comma);
            keyPairs.put(v, vm);
            keyPairs.put(b, bn);

            // bottom row: right half
            keyPairs.put(z, z_f_slash);
            keyPairs.put(x, x_dot);
            keyPairs.put(c, c_comma);
            keyPairs.put(m, vm);
            keyPairs.put(n, bn);
        }

        return keyPairs;
    }
}
