# Task 4 – Event-Handling Forensics: DrawPad Debugging

## Forensics & Repair Notes
* Misspelling `mouseClicked` as `mouseclicked` defines a separate method instead of overriding `MouseAdapter.mouseClicked`, and omitting the status label update leaves the click counter unchanged[cite: 15].
* Drawing directly with `getGraphics()` is transient because window minimize/restore or expose events trigger `paint(Graphics)` and clear unstored strokes[cite: 16].
* Storing clicked points in `List<Point>`, updating `status`, calling `repaint()`, and drawing all stored points inside `@Override public void paint(Graphics g)` preserves dots across repaints[cite: 16].
* Using `@Override` catches signature typos at compile time (`method does not override or implement a method from a supertype`), whereas omitting it allows silent compilation[cite: 16].
* `List<Point>` uses $O(n)$ memory and $O(n)$ repaint time for $n$ points; an off-screen `BufferedImage` achieves $O(1)$ repaint time at $O(\text{width} \times \text{height})$ pixel memory[cite: 18].
