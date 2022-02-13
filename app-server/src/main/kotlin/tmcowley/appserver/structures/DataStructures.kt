package tmcowley.appserver.structures;

import tmcowley.appserver.objects.Key;
import tmcowley.appserver.objects.KeyPair;

import tmcowley.appserver.structures.WordTree;
import tmcowley.appserver.structures.SentenceTree;

import java.io.File;


// todo

class DataStructures {

    public fun getPhraseList(): List<String> {
        val phraseSetFilePath = "./resources/phrases2.txt";

        var phraseSet = mutableListOf<String>()

        try{
            File(phraseSetFilePath).forEachLine { 
                phraseSet.add(it);
            }
        } catch (e: java.io.FileNotFoundException) {
            // handler
            println("Error: file: " + phraseSetFilePath + " not found.");
        }

        return phraseSet
    }

    public fun getWordSet(): HashSet<String> {

        val relativeFilePath = "./resources/words.txt";

        var allWords = hashSetOf<String>();

        try{
            File(relativeFilePath).forEachLine { 
                allWords.add(it);
            }
        } catch (e: java.io.FileNotFoundException) {
            // handler
            println("Error: file: " + relativeFilePath + " not found.");
        }

        return allWords;
    }

    fun getWords(currentWord: MutableList<KeyPair>): MutableList<String> {
        if (currentWord.isEmpty()) {
            return mutableListOf<String>()
        }
    
        // transform into tree
        val currentWordTree: WordTree = WordTree();
    
        for (keyPair: KeyPair in currentWord) {
            // println("adding keypair: ${keyPair}")
            currentWordTree.insert(keyPair);
        }
    
        return currentWordTree.findWords();
    }

    public fun getSentences(listOfMatchedWords: MutableList<MutableList<String>>): MutableList<String> {

        // store in Binary Tree, traverse each as before
        var sentenceTree: SentenceTree = SentenceTree();

        for (words: MutableList<String> in listOfMatchedWords) {
            sentenceTree.insert(words);
        }

        return sentenceTree.findWords();
    }

    public fun getKeyPairHashMap(): HashMap<Key, KeyPair> {

        // map keys
        val a: Key = Key('a');
        val b: Key = Key('b');
        val c: Key = Key('c');
        val d: Key = Key('d');
        val e: Key = Key('e');
        val f: Key = Key('f');
        val g: Key = Key('g');
        val h: Key = Key('h');
        val i: Key = Key('i');
        val j: Key = Key('j');
        val k: Key = Key('k');
        val l: Key = Key('l');
        val m: Key = Key('m');
        val n: Key = Key('n');
        val o: Key = Key('o');
        val p: Key = Key('p');
        val q: Key = Key('q');
        val r: Key = Key('r');
        val s: Key = Key('s');
        val t: Key = Key('t');
        val u: Key = Key('u');
        val v: Key = Key('v');
        val w: Key = Key('w');
        val x: Key = Key('x');
        val y: Key = Key('y');
        val z: Key = Key('z');

        // map key-pairs

        // top row
        val qp: KeyPair = KeyPair(q, p);
        val wo: KeyPair = KeyPair(w, o);
        val ei: KeyPair = KeyPair(e, i);
        val ru: KeyPair = KeyPair(r, u);
        val ty: KeyPair = KeyPair(t, y);

        // middle row
        // sort out dual chars w/ shift
        val a_colon: KeyPair = KeyPair(a, a);
        val sl: KeyPair = KeyPair(s, l);
        val dk: KeyPair = KeyPair(d, k);
        val fj: KeyPair = KeyPair(f, j);
        val gh: KeyPair = KeyPair(g, h);

        // bottom row
        val z_f_slash: KeyPair = KeyPair(z, z);
        val x_dot: KeyPair = KeyPair(x, x);
        val c_comma: KeyPair = KeyPair(c, c);
        val vm: KeyPair = KeyPair(v, m);
        val bn: KeyPair = KeyPair(b, n);

        // define key -> key-pair HM
        var keyPairs: HashMap<Key, KeyPair> = hashMapOf<Key, KeyPair>();
        
        // top row
        run {
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
        run {
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
        run {
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
