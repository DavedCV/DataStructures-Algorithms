public class Main {
    public static void main(String[] args) {
        HashTableSC<String, Integer> hash = new HashTableSC<>(40);

        hash.add("David", 1);
        hash.add("Liz", 2);
        hash.add("Mora", 3);
        hash.add("Nati", 4);
        hash.add("Ivan", 5);

        System.out.println(hash.keys());
        System.out.println(hash.values());
        System.out.println(hash);

        System.out.println("Remove David");
        hash.remove("David");
        System.out.println(hash.keys());
        System.out.println(hash.hasKey("David"));

    }
}