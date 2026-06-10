--- Day 4: Printing Department ---
You ride the escalator down to the printing department. They're clearly getting ready for Christmas; they have lots of large rolls of paper everywhere, and there's even a massive printer in the corner (to handle the really big print jobs).

Decorating here will be easy: they can make their own decorations. What you really need is a way to get further into the North Pole base while the elevators are offline.

"Actually, maybe we can help with that," one of the Elves replies when you ask for help. "We're pretty sure there's a cafeteria on the other side of the back wall. If we could break through the wall, you'd be able to keep moving. It's too bad all of our forklifts are so busy moving those big rolls of paper around."

If you can optimize the work the forklifts are doing, maybe they would have time to spare to break through the wall.

The rolls of paper (@) are arranged on a large grid; the Elves even have a helpful diagram (your puzzle input) indicating where everything is located.

For example:

..@@.@@@@.
@@@.@.@.@@
@@@@@.@.@@
@.@@@@..@.
@@.@@@@.@@
.@@@@@@@.@
.@.@.@.@@@
@.@@@.@@@@
.@@@@@@@@.
@.@.@@@.@.
The forklifts can only access a roll of paper if there are fewer than four rolls of paper in the eight adjacent positions. If you can figure out which rolls of paper the forklifts can access, they'll spend less time looking and more time breaking down the wall to the cafeteria.

In this example, there are 13 rolls of paper that can be accessed by a forklift (marked with x):

..xx.xx@x.
x@@.@.@.@@
@@@@@.x.@@
@.@@@@..@.
x@.@@@@.@x
.@@@@@@@.@
.@.@.@.@@@
x.@@@.@@@@
.@@@@@@@@.
x.x.@@@.x.
Consider your complete diagram of the paper roll locations. How many rolls of paper can be accessed by a forklift?

Your puzzle answer was 1516.

The DP approach — 2D prefix sum (summed-area table):

prefix[i+1][j+1] = roll(i,j) + prefix[i][j+1] + prefix[i+1][j] - prefix[i][j]

1. Build phase — one O(rows·cols) sweep fills prefix, where prefix[i][j] = number of rolls in the sub-grid above-and-left of (i,j). Each cell uses already-computed neighbours, the classic DP recurrence.
2. Query phase — the roll count inside any rectangle (so any cell's 3×3 window) becomes an O(1) lookup via inclusion–exclusion. Subtracting the centre cell gives the count of its 8 neighbours.

Total work is O(rows·cols) — linear in the number of cells — and crucially independent of the window size, unlike the naive "check 8 neighbours per cell" scan. The rollsInRect helper clamps bounds so edge/corner cells need no
special-casing. 

Notes:
- main first verifies against the marked example (asserts 13), then reads test_input.txt for the real answer — matching the file-reading style used in Day3Main.kt.
- Both @ and x count as rolls so the pre-marked example grid validates correctly; your actual input only contains @/..

(r1,c1) is the top-left corner of the rectangle and (r2,c2) the bottom-right, both inclusive.
                                                                                                                                                                                                                                   
---                                                                                                                                                                                                                              
The inclusion–exclusion idea

Recall prefix[i][j] = number of rolls in the whole block above and to the left of cell (i,j) (rows 0..i-1, cols 0..j-1).

To get just the rolls inside a rectangle, take the big block ending at the rectangle's bottom-right, then subtract the strips that stick out above and to the left, and add back the top-left block that got subtracted twice:

     A───────┬──────────┐                                                                                                                                                                                                          
     │   D   │    C      │      Rect = Full(bottom-right)                                                                                                                                                                          
     ├───────┼──────────┤             − Top strip                                                                                                                                                                                  
     │   B   │  RECT     │             − Left strip                                                                                                                                                                                
     └───────┴──────────┘             + Corner (D, removed twice)                                                                                                                                                                  

- prefix[bottom+1][right+1] → everything up to and including the rectangle (regions B+C+D+RECT)
- - prefix[top][right+1] → remove the strip above (C+D)
- - prefix[bottom+1][left] → remove the strip to the left (B+D)
- + prefix[top][left] → corner D was removed twice, add it back once

What remains is exactly RECT. The +1 offsets exist because prefix is 1-indexed (it has a row/column of zeros padded at the top and left), while grid coordinates are 0-indexed.
                                                                                                                                                                                                                                   
---                                                                                                                                                                                                                              
Concrete example

Take this 4×4 grid (@ = roll = counts as 1, . = 0):

col:   0 1 2 3                                                                                                                                                                                                                   
row 0: @ . @ @                                                                                                                                                                                                                   
row 1: . @ @ .                                                                                                                                                                                                                   
row 2: @ @ . @                                                                                                                                                                                                                   
row 3: . . @ @

The summed-area table prefix (size 5×5, first row/col are the zero padding):

          j=0  j=1  j=2  j=3  j=4                                                                                                                                                                                                  
i=0:     0    0    0    0    0                                                                                                                                                                                                   
i=1:     0    1    1    2    3                                                                                                                                                                                                   
i=2:     0    1    3    5    6                                                                                                                                                                                                   
i=3:     0    2    5    7    9                                                                                                                                                                                                   
i=4:     0    2    5    9   12

(e.g. prefix[2][3] = 5 means the top-left 2×3 block @ . @ / . @ @ holds 5 rolls — correct.)

Now query the 3×3 neighbourhood of cell (1,1) — that's rollsInRect(0, 0, 2, 2):

top=0, left=0, bottom=2, right=2

return prefix[3][3] - prefix[0][3] - prefix[3][0] + prefix[0][0]                                                                                                                                                                 
=     7        -     0        -     0        +     0                                                                                                                                                                        
=     7

Check by hand — the 3×3 block at rows 0–2, cols 0–2:                                                                                                                                                                             
@ . @                                                                                                                                                                                                                            
. @ @                                                                                                                                                                                                                            
@ @ .                                                                                                                                                                                                                            
Count the rolls: 1+0+1 + 0+1+1 + 1+1+0 = 7. ✓

Then in countAccessibleRolls, since cell (1,1) is itself a roll, we subtract 1 to get the neighbour count: 7 - 1 = 6 rolls around it. 6 is not < 4, so this roll is not forklift-accessible.
                                                                                                                                                                                                                                   
---                                                                                                                                                                                                                              
Why the maxOf/minOf clamping matters

For an edge cell like (0,0), the call is rollsInRect(-1, -1, 1, 1). The negative coordinates would index out of bounds, so:

top    = maxOf(0, -1) = 0                                                                                                                                                                                                        
left   = maxOf(0, -1) = 0                                                                                                                                                                                                        
bottom = minOf(3, 1)  = 1                                                                                                                                                                                                        
right  = minOf(3, 1)  = 1

The rectangle is clamped to the valid 0..1 × 0..1 window. This lets corner and border cells reuse the exact same code with no special-casing — off-grid neighbours simply contribute 0, which is the behaviour we want.


--- Part Two ---
Now, the Elves just need help accessing as much of the paper as they can.

Once a roll of paper can be accessed by a forklift, it can be removed. Once a roll of paper is removed, the forklifts might be able to access more rolls of paper, which they might also be able to remove. How many total rolls of paper could the Elves remove if they keep repeating this process?

Starting with the same example as above, here is one way you could remove as many rolls of paper as possible, using highlighted @ to indicate that a roll of paper is about to be removed, and using x to indicate that a roll of paper was just removed:

Initial state:
..@@.@@@@.
@@@.@.@.@@
@@@@@.@.@@
@.@@@@..@.
@@.@@@@.@@
.@@@@@@@.@
.@.@.@.@@@
@.@@@.@@@@
.@@@@@@@@.
@.@.@@@.@.

Remove 13 rolls of paper:
..xx.xx@x.
x@@.@.@.@@
@@@@@.x.@@
@.@@@@..@.
x@.@@@@.@x
.@@@@@@@.@
.@.@.@.@@@
x.@@@.@@@@
.@@@@@@@@.
x.x.@@@.x.

Remove 12 rolls of paper:
.......x..
.@@.x.x.@x
x@@@@...@@
x.@@@@..x.
.@.@@@@.x.
.x@@@@@@.x
.x.@.@.@@@
..@@@.@@@@
.x@@@@@@@.
....@@@...

Remove 7 rolls of paper:
..........
.x@.....x.
.@@@@...xx
..@@@@....
.x.@@@@...
..@@@@@@..
...@.@.@@x
..@@@.@@@@
..x@@@@@@.
....@@@...

Remove 5 rolls of paper:
..........
..x.......
.x@@@.....
..@@@@....
...@@@@...
..x@@@@@..
...@.@.@@.
..x@@.@@@x
...@@@@@@.
....@@@...

Remove 2 rolls of paper:
..........
..........
..x@@.....
..@@@@....
...@@@@...
...@@@@@..
...@.@.@@.
...@@.@@@.
...@@@@@x.
....@@@...

Remove 1 roll of paper:
..........
..........
...@@.....
..x@@@....
...@@@@...
...@@@@@..
...@.@.@@.
...@@.@@@.
...@@@@@..
....@@@...

Remove 1 roll of paper:
..........
..........
...x@.....
...@@@....
...@@@@...
...@@@@@..
...@.@.@@.
...@@.@@@.
...@@@@@..
....@@@...

Remove 1 roll of paper:
..........
..........
....x.....
...@@@....
...@@@@...
...@@@@@..
...@.@.@@.
...@@.@@@.
...@@@@@..
....@@@...

Remove 1 roll of paper:
..........
..........
..........
...x@@....
...@@@@...
...@@@@@..
...@.@.@@.
...@@.@@@.
...@@@@@..
....@@@...
Stop once no more rolls of paper are accessible by a forklift. In this example, a total of 43 rolls of paper can be removed.

Start with your original diagram. How many rolls of paper in total can be removed by the Elves and their forklifts?

Your puzzle answer was 9122.

The peel/cascade approach (countRemovableRolls) — a single linear BFS sweep:

A roll becomes accessible (removable) once fewer than four of its eight neighbours are rolls. Removing it lowers its neighbours' counts, which may make them removable, and so on, cascading until nothing is left accessible.

Key insight: removing rolls only ever decreases neighbour counts (monotone), so a roll that becomes accessible never becomes inaccessible again. The set of removable rolls is therefore well-defined and order-independent — there is no need to simulate the round-by-round removal shown above; one sweep finds the same total.

1. Count once — for each roll, compute its live roll-neighbour count (8 directions).
2. Seed the queue — enqueue every roll that already has < 4 roll-neighbours.
3. Peel — pop a roll, mark it removed, and decrement each present neighbour's count. The instant a neighbour's count crosses 4 -> 3 it has just become removable, so enqueue it then.

Each cell is removed at most once and each of its 8 edges is relaxed a constant number of times, so the whole process is O(rows·cols) — linear in the number of cells, the same complexity class as Part 1.

Implementation notes:
- Cells are encoded as r * cols + c integers in the ArrayDeque to avoid allocating pair objects in the hot loop.
- On pop the count is not re-checked: a cell is only ever enqueued while its count is already < 4, and counts never rise, so it is guaranteed still removable. The `if (!present[r][c]) continue` guard harmlessly skips a cell reached through two paths.
- The 4 -> 3 crossing test (`--neighbourCount == 3`) ensures each cell is enqueued at most once via the relaxation step; cells that start below the threshold are handled by the initial seeding instead.

---
Worked example — the threshold crossing

Suppose a roll currently has 5 roll-neighbours. As neighbours are removed its count drops 5 -> 4 -> 3. Nothing happens on the 5 -> 4 step (still inaccessible). On the 4 -> 3 step it becomes accessible, so that is exactly when we enqueue it. Further drops (3 -> 2 -> 1 -> 0) change nothing because it is already queued/removed. This is why the enqueue test is `== 3` rather than `< 4`: it fires once, on the single transition that matters.

---                                                                                                                                                                                                                              
The two things that make it correct and linear

1. Order doesn't matter. Removing rolls only ever lowers neighbour counts — counts never go back up. So once a cell becomes removable it stays removable; the final removed set is the same no matter what order you pop the     
   queue. That's why one BFS sweep equals the round-by-round simulation.
2. The == 3 (4→3) test, not < 4. A locked cell at count 5 drops 5→4→3 as neighbours vanish. Nothing should happen on 5→4 (still locked); it should be enqueued once, exactly on 4→3. Testing --count == 3 fires on that single   
   transition. Further drops (3→2→1→0) are ignored because it's already queued. Cells that begin below the threshold are caught by the initial seeding instead. This "enqueue once" property is what keeps the work at O(rows·cols)
   — each cell removed once, each of its 8 edges relaxed a constant number of times.

The if (!present[r][c]) continue guard on pop is just a safety net for a cell that gets queued and then... actually can't be queued twice here, but the guard makes the code robust to that without re-checking the count (a     
queued cell's count is always already < 4 and never rises, so it's guaranteed still removable when popped).

I'll walk through Part 2 with a small, self-contained example: a solid 3×3 block of rolls. It's small enough to trace by hand but still shows the full cascade.

col:   0 1 2
row 0: @ @ @
row 1: @ @ @
row 2: @ @ @

Rule recap: a roll is removable when fewer than 4 of its 8 neighbours are rolls.

---
Step 1 — initial neighbour counts

Count how many of each cell's 8 neighbours are rolls:

3 5 3
5 8 5
3 5 3

- Corners (4 of them) → 3 neighbours each → 3 < 4 �→ removable now
- Edge-middles (4 of them) → 5 neighbours → locked
- Center → 8 neighbours → locked

So initially only the 4 corners can be reached by a forklift.

---
The intuitive "rounds" view (what the README illustrates)

Round 1 — remove the 4 corners. The remaining rolls form a plus shape:

. @ .
@ @ @
. @ .

Recount the survivors:
- each edge-middle now has only 3 roll-neighbours → removable
- the center now has exactly 4 → still locked (4 is not < 4)

Round 2 — remove the 4 edge-middles. Only the center is left:

. . .
. @ .
. . .

- center now has 0 neighbours → removable

Round 3 — remove the center.

Total removed = 4 + 4 + 1 = 9 (the whole block dissolves).

---
How the code gets the same 9 without simulating rounds

countRemovableRolls doesn't track rounds. It keeps a mutable neighbour count per cell and a queue, and decrements counts as rolls disappear. The crucial detail: a cell is enqueued the instant its count crosses 4 → 3, because that's the single moment it flips from locked to removable.

Let me trace the important events (cells encoded by (r,c)):

Seed: the 4 corners already have count < 4, so the queue starts as
[(0,0), (0,2), (2,0), (2,2)], removed = 0.

Pop (0,0) → remove (removed=1). Decrement its present neighbours:
- (0,1): 5→4, (1,0): 5→4, (1,1): 8→7 — none hit 3, nothing enqueued.

Pop (0,2) → remove (removed=2). Neighbours:
- (0,1): 4→3 ← crossed the threshold → enqueue (0,1)
- (1,1): 7→6, (1,2): 5→4

Pop (2,0) → remove (removed=3).
- (1,0): 4→3 → enqueue (1,0)
- (1,1): 6→5, (2,1): 5→4

Pop (2,2) → remove (removed=4).
- (1,2): 4→3 → enqueue (1,2)
- (2,1): 4→3 → enqueue (2,1)
- (1,1): 5→4

Queue is now [(0,1), (1,0), (1,2), (2,1)] — exactly the four edge-middles that "Round 2" unlocked, discovered automatically.

Pop (0,1) → remove (removed=5). Decrement present neighbours:
- (1,0): 3→2, (1,2): 3→2, and (1,1): 4→3 → enqueue (1,1) ← the center finally unlocks

Pop (1,0) → remove (removed=6). (1,1): 3→2, (2,1): 3→2. (1,1) already queued — and the == 3 test only fires on the 4→3 crossing, so it isn't enqueued twice.)

Pop (1,2) → remove (removed=7). (1,1): 2→1, (2,1): 2→1.

Pop (2,1) → remove (removed=8). (1,1): 1→0.

Pop (1,1) → remove (removed=9). Queue empty → done, 9 removed. ✓

---
The two things that make it correct and linear

1. Order doesn't matter. Removing rolls only ever lowers neighbour counts — counts never go back up. So once a cell becomes removable it stays removable; the final removed set is the same no matter what order you pop the queue. That's why one BFS sweep equals the round-by-round simulation.
2. The == 3 (4→3) test, not < 4. A locked cell at count 5 drops 5→4→3 as neighbours vanish. Nothing should happen on 5→4 (still locked); it should be enqueued once, exactly on 4→3. Testing --count == 3 fires on that single transition. Further drops (3→2→1→0) are ignored because it's already queued. Cells that begin below the threshold are caught by the initial seeding instead. This "enqueue once" property is what keeps the work at O(rows·cols) — each cell removed once, each of its 8 edges relaxed a constant number of times.

The if (!present[r][c]) continue guard on pop is just a safety net for a cell that gets queued and then... actually can't be queued twice here, but the guard makes the code robust to that without re-checking the count (a queued cell's count is always already < 4 and never rises, so it's guaranteed still removable when popped).