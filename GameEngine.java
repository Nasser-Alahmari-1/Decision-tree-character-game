import java.io.*;
import java.util.*;

public class GameEngine {
    private Node root;
    private int questionCount;

    // ─── Node in our decision tree ───────────────────────────────────
    private static class Node {
        boolean isLeaf;
        Person person;      // only for leaves
        String attr, val;   // which attribute / value we test
        String question;    // English question to ask
        Node yes, no;       // subtrees

        // leaf
        Node(Person p) {
            isLeaf = true;
            person = p;
        }

        // internal node
        Node(String attr, String val, String question, Node yes, Node no) {
            isLeaf   = false;
            this.attr     = attr;
            this.val      = val;
            this.question = question;
            this.yes      = yes;
            this.no       = no;
        }
    }

    // ─── Construct from CSV ─────────────────────────────────────────
    public GameEngine(String csvPath) {
        List<Person> all = readCSV(csvPath);
        if (all.isEmpty()) {
            throw new IllegalArgumentException("CSV had no data!");
        }
        root = buildTree(all);
    }

    // ─── 1) Read CSV into Person list ──────────────────────────────
    private List<Person> readCSV(String path) {
        List<Person> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] d = line.split(",");
                if (d.length >= 8) {
                    list.add(new Person(d));
                }
            }
        } catch (IOException e) {
            System.err.println("CSV error: " + e.getMessage());
            System.exit(1);
        }
        return list;
    }

    // ─── 2) Recursively build a balanced yes/no BST ─────────────────
    private Node buildTree(List<Person> list) {
        int n = list.size();
        // Base: single person
        if (n == 1) {
            return new Node(list.get(0));
        }

        // Attempt to find the best yes/no split on attributes
        String bestAttr = "", bestVal = "", bestQ = "";
        int    bestDiff = Integer.MAX_VALUE;

        // a) Boolean splits
        int yesAlive = 0, yesRoyal = 0;
        for (Person p : list) {
            if (p.alive)   yesAlive++;
            if (p.royalty) yesRoyal++;
        }
        // alive?
        if (yesAlive>0 && yesAlive<n) {
            int d = Math.abs(yesAlive - (n - yesAlive));
            if (d < bestDiff) {
                bestDiff = d;
                bestAttr = "alive";
                bestVal  = "true";
                bestQ    = "Is the person alive?";
            }
        }
        // royalty?
        if (yesRoyal>0 && yesRoyal<n) {
            int d = Math.abs(yesRoyal - (n - yesRoyal));
            if (d < bestDiff) {
                bestDiff = d;
                bestAttr = "royalty";
                bestVal  = "true";
                bestQ    = "Is the person royalty?";
            }
        }

        // b) Categorical splits
        String[] cats = { "gender","ageGroup","famousFor","nationality","religion" };
        for (String attr : cats) {
            Map<String,Integer> freq = new HashMap<>();
            for (Person p : list) {
                String v = switch(attr) {
                    case "gender"      -> p.gender;
                    case "ageGroup"    -> p.ageGroup;
                    case "famousFor"   -> p.famousFor;
                    case "nationality" -> p.nationality;
                    case "religion"    -> p.religion;
                    default            -> "";
                };
                freq.put(v, freq.getOrDefault(v,0)+1);
            }
            for (var e : freq.entrySet()) {
                int cnt = e.getValue();
                if (cnt>0 && cnt<n) {
                    int d = Math.abs(cnt - (n-cnt));
                    if (d < bestDiff) {
                        bestDiff = d;
                        bestAttr = attr;
                        bestVal  = e.getKey();
                        bestQ    = switch(attr) {
                            case "gender"      -> "Is the person " + bestVal + "?";
                            case "ageGroup"    -> "Is the person's age group " + bestVal + "?";
                            case "famousFor"   -> "Is the person famous for " + bestVal + "?";
                            case "nationality" -> "Is the person from " + bestVal + "?";
                            case "religion"    -> "Is the person's religion " + bestVal + "?";
                            default            -> "";
                        };
                    }
                }
            }
        }

        // c) If no attribute split was possible (all share same attrs),
        //    fall back to asking the **name** directly.
        if (bestAttr.isEmpty()) {
            // ask name of the first person
            String nm = list.get(0).name;
            String q  = "Is your character " + nm + "?";
            Node yesLeaf = new Node(list.get(0));
            Node noLeaf;
            if (list.size() == 2) {
                noLeaf = new Node(list.get(1));
            } else {
                // if more than 2 share identical attrs, recurse on the rest
                noLeaf = buildTree(list.subList(1, list.size()));
            }
            return new Node("name", nm, q, yesLeaf, noLeaf);
        }

        // d) Partition into yes/no lists
        List<Person> yesList = new ArrayList<>(), noList = new ArrayList<>();
        for (Person p : list) {
            boolean match = switch(bestAttr) {
                case "alive"       -> p.alive;
                case "royalty"     -> p.royalty;
                case "gender"      -> p.gender.equals(bestVal);
                case "ageGroup"    -> p.ageGroup.equals(bestVal);
                case "famousFor"   -> p.famousFor.equals(bestVal);
                case "nationality" -> p.nationality.equals(bestVal);
                case "religion"    -> p.religion.equals(bestVal);
                default            -> false;
            };
            if (match) yesList.add(p);
            else        noList.add(p);
        }

        // Guard against an empty branch
        Node yesNode = yesList.isEmpty() ? new Node(list.get(0)) : buildTree(yesList);
        Node noNode  = noList .isEmpty() ? new Node(list.get(0)) : buildTree(noList);

        return new Node(bestAttr, bestVal, bestQ, yesNode, noNode);
    }

    // ─── 3) Play by walking the tree ────────────────────────────────
    public void play() {
        questionCount = 0;
        Scanner sc = new Scanner(System.in);
        Node node = root;

        while (!node.isLeaf) {
            questionCount++;
            int y = count(node.yes), n = count(node.no);
            System.out.printf("%s  (remaining %d/%d)%n",
                    node.question, y, n);

            String ans = sc.nextLine().trim().toLowerCase();
            node = ans.equals("yes") ? node.yes : node.no;
        }

        // final name confirmation
        System.out.printf("Is your character %s? (yes/no)%n",
                node.person.name);
        String f = sc.nextLine().trim().toLowerCase();
        if (f.equals("yes")) {
            System.out.printf("Found in %d questions!%n",
                    questionCount);
        } else {
            System.out.printf("Stopped after %d questions—no match.%n",
                    questionCount);
        }
        System.out.println("Ideal best: 6, Worst: 50");
    }

    // count leaves under a subtree
    private int count(Node n) {
        if (n.isLeaf) return 1;
        return count(n.yes) + count(n.no);
    }
}
