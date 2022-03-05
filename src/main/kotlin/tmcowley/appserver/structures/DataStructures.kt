package tmcowley.appserver.structures;

import tmcowley.appserver.objects.Key;
import tmcowley.appserver.objects.KeyPair;

import tmcowley.appserver.structures.WordTree;
import tmcowley.appserver.structures.SentenceTree;

import java.io.File;

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import kotlin.text.toIntOrNull

class DataStructures {

    fun getPhraseList(): List<String> {
        val path = "./resources/phrases2.txt";

        var phraseSet = mutableListOf<String>()

        try{
            File(path).forEachLine { 
                phraseSet.add(it);
            }
        } catch (e: java.io.FileNotFoundException) {
            // handler
            println("Error: file: ${path} not found.");
        }

        return phraseSet
    }

    fun getWordFrequencies(): HashMap<String, Int> {
        val path = "./resources/word-frequencies.csv";

        var wordFreqLookup = HashMap<String, Int>()

        try{
            // parse csv
            csvReader().open(path) {
                readAllWithHeaderAsSequence().forEach { row: Map<String, String> ->
                    // add word and frequency to lookup
                    wordFreqLookup.put(row.get("Word")!!.lowercase(), row.get("FREQcount")!!.toInt())
                }
            }
        } catch (e: java.io.FileNotFoundException) {
            // handler
            println("Error: file: ${path} not found.");
        }

        return wordFreqLookup
    }

    fun getWordSet(): HashSet<String> {

        val path = "./resources/words.txt";

        var allWords = hashSetOf<String>();

        // add words from words.txt
        run {
            try{
                File(path).forEachLine { 
                    allWords.add(it);
                }
            } catch (e: java.io.FileNotFoundException) {
                // handler
                println("Error: file: ${path} not found.");
            }
        }

        // TODO: debug (names, places not added to dictionary (e.g. "canada", "mary"))
        // maybe the phrase list is empty? 

        // add words from phrase list
        run {
            val phraseList: List<String> = getPhraseList();
            for (phrase: String in phraseList) {
                for (word: String in phrase.split(" ")) {
                    allWords.add(word.lowercase());
                }
            }
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

    fun getSentences(listOfMatchedWords: MutableList<MutableList<String>>): MutableList<String> {

        // store in Binary Tree, traverse each as before
        var sentenceTree: SentenceTree = SentenceTree();

        for (words: MutableList<String> in listOfMatchedWords) {
            sentenceTree.insert(words);
        }

        return sentenceTree.findWords();
    }

    fun getKeyPairHashMap(): HashMap<Key, KeyPair> {

        // map keys

        // var charToKeyLookup = HashMap<Char, Key> ();

        // val alphabet = ('a'..'z').toMutableList()
        // for (char in alphabet) {
        //     charToKeyLookup.put(char, Key(char));
        // }

        // val a: Key = Key('a');
        // val b: Key = Key('b');
        // val c: Key = Key('c');
        // val d: Key = Key('d');
        // val e: Key = Key('e');
        // val f: Key = Key('f');
        // val g: Key = Key('g');
        // val h: Key = Key('h');
        // val i: Key = Key('i');
        // val j: Key = Key('j');
        // val k: Key = Key('k');
        // val l: Key = Key('l');
        // val m: Key = Key('m');
        // val n: Key = Key('n');
        // val o: Key = Key('o');
        // val p: Key = Key('p');
        // val q: Key = Key('q');
        // val r: Key = Key('r');
        // val s: Key = Key('s');
        // val t: Key = Key('t');
        // val u: Key = Key('u');
        // val v: Key = Key('v');
        // val w: Key = Key('w');
        // val x: Key = Key('x');
        // val y: Key = Key('y');
        // val z: Key = Key('z');

        // map key-pairs

        // top row
        // val qp: KeyPair = KeyPair(charToKeyLookup.get('q')!!, charToKeyLookup.get('q')!!);
        val qp: KeyPair = KeyPair('q', 'p');
        val wo: KeyPair = KeyPair('w', 'o');
        val ei: KeyPair = KeyPair('e', 'i');
        val ru: KeyPair = KeyPair('r', 'u');
        val ty: KeyPair = KeyPair('t', 'y');

        // middle row
        // sort out dual chars w/ shift
        val a_colon: KeyPair = KeyPair('a', 'a');
        val sl: KeyPair = KeyPair('s', 'l');
        val dk: KeyPair = KeyPair('d', 'k');
        val fj: KeyPair = KeyPair('f', 'j');
        val gh: KeyPair = KeyPair('g', 'h');

        // bottom row
        val z_f_slash: KeyPair = KeyPair('z', 'z');
        val x_dot: KeyPair = KeyPair('x', 'x');
        val c_comma: KeyPair = KeyPair('c', 'c');
        val vm: KeyPair = KeyPair('v', 'm');
        val bn: KeyPair = KeyPair('b', 'n');

        // define key -> key-pair HM
        var keyPairs: HashMap<Key, KeyPair> = hashMapOf<Key, KeyPair>();
        
        // top row
        run {
            // top row: left half
            keyPairs.put(Key('q'), qp);
            keyPairs.put(Key('w'), wo);
            keyPairs.put(Key('e'), ei);
            keyPairs.put(Key('r'), ru);
            keyPairs.put(Key('t'), ty);

            // top row: right half
            keyPairs.put(Key('p'), qp);
            keyPairs.put(Key('o'), wo);
            keyPairs.put(Key('i'), ei);
            keyPairs.put(Key('u'), ru);
            keyPairs.put(Key('y'), ty);
        }

        // middle row
        run {
            // middle row: left half
            keyPairs.put(Key('a'), a_colon);
            keyPairs.put(Key('s'), sl);
            keyPairs.put(Key('d'), dk);
            keyPairs.put(Key('f'), fj);
            keyPairs.put(Key('g'), gh);

            // middle row: right half
            keyPairs.put(Key('a'), a_colon);
            keyPairs.put(Key('l'), sl);
            keyPairs.put(Key('k'), dk);
            keyPairs.put(Key('j'), fj);
            keyPairs.put(Key('h'), gh);
        }

        // bottom row
        run {
            // bottom row: left half
            keyPairs.put(Key('z'), z_f_slash);
            keyPairs.put(Key('x'), x_dot);
            keyPairs.put(Key('c'), c_comma);
            keyPairs.put(Key('v'), vm);
            keyPairs.put(Key('b'), bn);

            // bottom row: right half
            keyPairs.put(Key('z'), z_f_slash);
            keyPairs.put(Key('x'), x_dot);
            keyPairs.put(Key('c'), c_comma);
            keyPairs.put(Key('m'), vm);
            keyPairs.put(Key('n'), bn);
        }

        return keyPairs;
    }


}
