# Harry's Notes

## Scan System

Currently, I have a scan look up system for components. its basically one large table
with all the entities in the world and their components. This is very inefficient
for queries because currently, the query will scan every single entity in the world
and check if it satisfies the query.

If I instead group entities by their component makeup I can have the query look up
and return just the archetypes that satisfy its condition. On top of that I can
cache the query and only requery when one of the archetypes associated with that
query is modified.

## ArcheType and Component System

I am discovering the benefits to encoding components internally for loop up
purposes.

An archetype is a unique combination of components that a set of entities share.
For example (Position, Velocity, Collider) would be one while just (Position, Velocity)
would be another.

The archetype storage will house an archetype and the entities that belong to it.
It will look kind of like this

| slot | EntityHandle | Position | Velocity | Collider |
|------|--------------|----------|----------|----------|
| 0    | EntityA      | EntityA  | EntityA  | EntityA  |
| 1    | EntiyB       | EntityB  | EntityB  | EntityB  |
| 2    | EntiyC       | EntityC  | EntityC  | EntityC  |

The archetype manager will be responsible for manipulating the entities in
each archetype storage based on their component makeup at runtime

if world.removeComponent (entityB, Collider);
the entity would have to be taken out of the (Position, Velocity, Collider)
archetype and moved to the (Position, Velocity) archetype.

## Encoding/Regisering Components

My first idea was to Hash the archetypes themselves for look up. The User provides
the components they want to query, the system hashes them and finds the correct entities
and components. But this has a big weakness.

- Having to Hash an archetype takes several steps
- If the query is not in the exact order as the storage the hash will not be the same (mutable hashes are no good)

The solution is to map the components internally to a unique id. If I make

- position = 1
- velocity = 2
- collision = 3

We now have a way to make unique arrays that can be sorted that directly correspond with an archetype.
For example...

- [1, 2].sort () = (position, velocity)
- [2, 1].sort () = (position, velocity)
- [3, 2].sort () = (velocity, collision)

Even if the query is not the same order as the storage, the order is preserved
which will make the query able
to compare to the archetype with confidence and speed.

Now actually implementing this is hurting my brain.

Below is my first stab at the archetype class which is a HashSet of components,
but this would not work if I am changing internally how components are represented (with integers).

```java
class Archetype {
    private final Set<Class<? extends Component>> componentClasses;

    Archetype(Set<Class<? extends Component>> componentClasses) {
        this.componentClasses = componentClasses;
    }
}
```

Imagine the user wants to write ```word.addComponent(entityHandle, PositionComponent.class);```

Figure out how to:

1) Check if the component is already in the registry
2) If it is not in the component registry, add it.
3) If it is in the registry, or it was just added. Convert it to its unique integer
4) Find the entity in the archetype storage
5) Move the entity to its archetype storage location that reflects
   its new archetype after Position component was added
6) If that archetype does not exist, add it to the archetype registry

The most challenging part I think will be step 4. The way the data structure is set up in my
head is that there will be an integer array (archetype) representing a Hash key to the
component structure in the table above. So [1, 2] represents the position and velocity archetype,
and [1, 3] represents the position and collision archetype. Maybe [0] will represent entities
that do not have any components. So the problem is, if my key to get access to the entities is
its archetype, how do I enter the data structure only knowing some of the components?

If I add the component with the addComponent () method, how does the archetype storage
able to perform and operation like archetypeStorage.getEntitesArchetype (entityHandle).
If the only way to get into the storage is the archetype hash?

Do I need an array of all entities that holds their archetype array (integer array)? If I do that
now I have to update the entity's archetype in two places and keep them synced. This might not be
a bad idea. If one data structure is designed for quick access for components and the other is designed
for quick access to the entities archetype then I don't see why not.

I am imagining something like this...

int[][] where the first dimension is the entityId and the second is its current archetype code

```java
int[][] entityArchetypes = {
        {1, 2, 2}, // entity 0 (position, velocity, collider)
        {1, 3}, // entity 1 (position, collider)
        {0} // entity 2 no components 
};
```

now I can use that to get the hash key to get access to the archetype structure
I think this should live in the archetype manage so that it can stay synced with
the archetype storage.

I see a problem where I have the entities and their generations along with available slots
managed in the EntityManager, and I have the entities and their Archetypes managed in the
ArchetypeManager, and I have the entities and their components managed in the Archetype manager.
I have three places that need to be synced with each other, just something to think about.

What if I just encode more data into the archetype array!!!!!!!!!! Check this out

What if I do

```java
// index = id
// generation = entitiesMetadata[entityId][0]
// archetype = entitiesMetadata[entityId[1..array.length]
```

keeping that data contained in a 2DArray. I'm going to stop thinking now and actually
build it.

## Remember what a handle means Harry!

I am so tempted to give the entity handle more data attached to it. I have to keep reminding
myself what is the purpose of the handle. It is just a way to tell the ECS operate on this
entity. It is NOT an authoritative data structure holding state. If the handle was used to
hold state, there is a chance that the handle becomes invalid (because the entity does not exist, or
the entities properties changed). If User systems operated on the entity assuming the entity handle
holds state, it would crumble the whole ECS system.

Instead, handles are a way to politely ask the ECS "Hey buddy, here is an entity I would like to
manipulate can you do it?" and the ECS can respond "Yeah it's still here" or "Sorry bruv, that entity
actually no longer exists" or "Mate, that component does not exist on that entity anymore".

## Switching to an internal ValidatedEntityHandle

I was having a problem enforcing a valid entity handle when it is passed to another function from
the public API. After some thoughts I've decided that having an internal Type called
ValidatedEntityHandle that all my internal methods take. Will be useful in making sure that I
filter the entity handle before it is given to anything. I just now need to be careful not
to make the lifetime of that type longer than it has to be because I could end up using it
thinking its valid when somewhere else invalidated it. It shouldn't be a problem since I am
marking entities for destruction and flushing them at the end of the frame instead, so all
computations and queries can still be done with "dead" entities. Soooooo in theory they should
all be "valid" until the end of the frame. Watch this bite me in the butt later.

## Component Registrar

the component manger is going to have a registrar that will take in a component class and either
give back the component id associated with it, or create a new id if the component has not be
registered yet. This dynamic solution makes it so the user does not need to mannually register
components with the ECS.

Instead, they will simply

- world.addComponent (entity, SomeComponent.class) then
- componentManager.registerOrConvert (SomeComponent.class)
- componentRegistrar.isRegistered (SomeComponent.class) if not
- componentRegistrar.register (SomeComponent.cass)
- componentRegistrar.getId (SomeComponent.class)

Something along those lines at least for the registration part. The archetype part is a whole
other beast.

Look into eclipse Hashmaps for very fast hashing of primitive int arrays.
currently Java libraries do not have a fast solution to using primitive arrays as keys.

import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.block.factory.HashingStrategies;
import org.eclipse.collections.impl.map.mutable.UnifiedMapWithHashingStrategy;

## The Meta Philosophy of ECS

As i ponder the characteristics of my ECS engine, I find myself butting heads with "What
should be a rule of the engine" and "What should be the responsibility of the user".

For example, if I wanted a cooldown timer to say, this is the nex time it will shoot, I would simply add the
CooldownComponent to that entity, query the component, and tick down the timer. But what if I wanted to
add a jump cool down, and maybe a drink cool down? Now my dumbass has to think about how my ECS would handle
this. Would I add a JumpCooldownComponent and a DrinkCooldownComponent to the entity? If I did, how would I query
for these components? I could now say query (Velocity, JumpCooldown) and that would be convient for associating the
type of cool down with the component its with. but the issue is when I want to update ALL cool downs, would I say
query (JumpCooldown, DrinkCooldown, WeaponCooldown, RunningCooldown, DamageCooldown, ManaCooldown) you can see how this
could get cumbersome.

The question that im always trying to answer is "Should I build this solution into the ECS itself or let the User decide
how to handle it given simple ECS rules." in other worlds "how much of a framework do I want to make"

One solution to the cool down problem is to implement a "Traits" concept. when I world.addComponent (JumpCooldown.class)
.trait (Coolddown.class)
and the query system can now say query.containtsTrait (Cooldown). somewhere in the ECS machinery it can register user
made component categories
as "Traits" and from there can query.

Something about that solution irks me. It feels like I'm now making design decision for the user that they never asked
for.

A solution Garry came up with is to have one CooldownComponent with a Map<type,float> value. I do not like this either
because
we are now undermining the ECS query system and adding another layer into component manipulation. Although this solution
is
user based and not engine based. So actually im fine with this because it does not change the architecture of my ECS.

I was thinking "If I was a user of this library, how would I solve, needing multiple timers, for the same entity?"
assuming the ECS does not solve
this. Then I though, " well maybe needing multiple cooldowns for the same entity means that they are NOT the same
entity!" EUREKA! maybe... does
a player entity need a WeaponCooldownComponent or can the weapon entity have just a Cooldown timer contextually mapping
the timer to the weapon.

But surely a player is one entity and needs both a JumpCooldown and a DrinkCooldown right? That is where I got stuck.
Luckily I ask Garry for their
opinion.

Garry said, "If you think about it, an Ability can be an entity". mic drop. didn't even occur to me, thanks Garry.

Basically there can be an entity JumpAbility or DrinkAbility. These entities can each have a CoolDownComponent, and, a
Jump and Drink component
respectively. Here is the kicker, we can now associate entities with each other with an OwnerComponent. Now the
JumpAbility entity can be
owned by a player entity, and just like real life, a player may LOSE the ability to jump. Oh, baby a triple!

I am a big fan of this solution because it keeps the ECS API and implementation simple, giving users solutions and
flexibility. Now you can build
complexity from simplicity rather than starting with complexity to make one way of doing things simple. Think LEGO
before they added all the weird
dinosaur bodies that weren't even creatively contracted with lego. I mean come on dude, you had all these cool small
lego pieces, and you decided to
instead injection mold a huge premade dinosaur body that only has the foot and top attachment points. pathetic... That's
what Papa called limiting complexity.

## what does it mean for a handle to be "valid"

Design issue I am wrestling with. I have a method in EntityManager

```java
ValidEntityHandle validate(EntityHandle handle) {

    if (handle == null) {
        throw new NullPointerException("Entity handle can not be null");
    }

    if (handle.generation() != generation[handle.id()]) {
        throw new IllegalArgumentException("Entity does not exist");
    }

    return new ValidEntityHandle(handle.id(), handle.generation());
}
```

Its original design was to take a handle from the user side and resolve if the handle was able to be used on the ECS
side. It checks for null. and checks if the entity generation is the same, if they are not, it means the entity does not
"exist" within the ECS context and can't be operated on.

I was thinking the problem was, how do ask world.isDestroyed (entityHandle) without throwing an
exception when it no longer exists in the game.

For a single frame, before all the entites marked for distruction are resolved they can still be queried and operated on
just as if they were alive. they have not truley been removed from the ECS system just tagged with "they gonna die
soon". so
when a user asks isDestroyed (entityHandle) and that entity is already distroyed maybe a frame earlier, it should return
true. I
think I'm trying
to figure out if the validate method should throw an error or not if the entity does not exist in the ECS context.

## Destroying Entities and Differed Destruction

I have spent too much time pondering the efficacy of differing destruction of entities or destroying them on the spot
when the user
says world.destroy (entity). To clarify, by differed destruction I mean this, the entity is "destroyed" but actually in
the ECS it
is marked or pending destruction. Then at the end of the frame the ECS "flushes" all the entities pending destruction.

I assumed that differed destruction was paramount to the functioning of the ECS. I
did not even give it a second thought. Logically I thought, "If the entity is destroyed before all the systems have
finished then something
will break". I took this thought for granted. When I sat down and started writing out my assumption, working through
actual scenarios
in my head, the notion that "I need differed destruction or the ECS breaks" started to not make sense.

If I can just take you through my thought chain for a second. If I were to do `world.query(Posistion.class)`, I would
get the state of all members
of that query at the time the query was performed. Then I say `world.removeComponent(entity, Position.class)` within the
same system. Now the reference
inside the ECS is removed but the query still has the reference to that component.

Before...

ECS.storage ==> PositionA

query ==> PositionA

world.removeComonent (PositionA);

ECS.storage ==> Null

query ==> PositionA

The system can still use that component to manipulate state. This sounded bad to me at first, but I tried to reason this
way and that, and came to
the conclusion that it's actually a NON-issue. The manipulation of an object that will be cleared out by the JVM anyway
seems like not my problem.

The next natural thought is. "Okay removing the entity from the ECS mid-system is not integral to the functioning of the
ECS... but wouldn't it be
nice to be able to access the 'destroyed' entity's state later down the chain of systems?". I see that actually as a
strong argument and worth
exploring.

For example, say a goblin dies and is world.detroy (goblin) in the DamageSystem. That goblin had loot, the player wants
that loot, but a later system that
wants to lay the loot on the ground can't anymore because the goblin and its components were removed from the ECS. So
one solution is to make a OnDeath Event
inside the damage system. But now death event has to hold the loot data! GROSS SMELL!

Well, you ask, how do we keep entity data around for later processing even after the ECS destroyed it? Differed
destruction of course. the user
marks it for destruction and the ECS will flush it out of the system at the end of the frame. Problem solved.

Hold the phone! What is the goal here? What am I building? Is this ECS specifically for games? It would be a nice
feature for a game? The most
important question of all... Does this feature limit or expand the options for using this library.

My mission if I had to make one is: To make an Entity Component System that is used as a tool to
store and manipulate state in Java. Not to make a game, although it is what I want to do with it.
Keeping this in mind it is clear that adding differed destruction is a "quality of life" feature
aimed at game developers and not a necessary feature for the functioning of the ECS, let me explain

Using the ECS framework in its current state. A user could say in the `DamageSystem`
`world.query(Collider, Damage, Health` then use a component to manually differ the destruction
later with `world.addComonent(entity, PendingDestruction.class)`. The entity is still exists
within the ECS world and is marked for destruction without a dedicated ECS method getting involved.
Now later `DropLootSystem` can query `world.query(PendingDestruction.class, Inventory.class)` and do what they want with
data,
like in the earlier example, drop loot on the ground. At the very end the user can say in a
`CleanupSystem` `world.query(PendingDestruction.class)` and then `foreach(world.destroy(entity))`.

You can see how now the ECS framework is not enforcing what it means to be dead or alive. Instead of
`world.destroy()` actually meaning "destroy this at the end of the frame" its literally saying
"remove this entity from the ECS entirely so it cannot access it anymore". That sets up a very
clear contract. Destroy mean destroy in the context if the ECS, if the user wants to do some freaky
stuff with the data after its "death", then don't remove it from the ECS just yet, mark it
yourself, and manually clean it up.

### A reminder to myself about design decisions.

I have to keep telling myself, why do I keep assuming this is the best way of solving this problem. Other
people statistically are much better at building complexity. My job is to give simple building blocks
and let the users build the complexity. Look at Minecraft, Notch gave players block that looked different
with unique properties,
that's literally it, and players took those blocks and created flying machines, automatic mining machines,
and mob farms. Imagine if I felt I had to program something as complex as a mining machine into my ECS
framework because I thought it would be useful. Instead, I just supply the blocks and let the users decide
how they fit together.

This exact problem came up when they were designing instruction sets for processors. One side said we should
group frequent instruction into one instruction. The other side said just keep as few basic instructions
as possible and let programmers build the complexity and groups. Well, as you may know, the side that wanted
a small set of basic instructions won because the side that wanted to group instructions by frequency use
assumed that they understood what those groups were. There were so many possible groups of instructions
that it was impossible to know what to include in the finite instruction set. It is better to give the building
blocks than to assume this is what they want to build with it. At least I think that's how the story goes,
I'll have to fact-check it later.

Anyway the moral of the story is... Decide, what is a building block and what can be built by building blocks.
Then I will know what goes in or out of the ECS.

## In the Hot Loop!

This is my original thoughts for how I would add multiple components at a time

```java
void addComponents(ValidEntityHandle handle, ComponentId... componentIds) {

    int entityId = handle.id();

    Arrays.sort(componentIds, ComponentId::compareTo);

    for (ComponentId componentId : componentIds) {

        int rawComponentId = componentId.value();

        int result = Arrays.binarySearch(archetypes[entityId], rawComponentId);

        if (result >= 0) {
            throw new IllegalArgumentException("Error: Cannot add duplicate Components");
        }

        // loot at return of Arrays.binarysearch to find out why we need to manipulate
        // result in this way
        int insertionPoint = -(result + 1);

        // append component id
        int[] currentArchetype = getArchetype(entityId);
        int[] newArchetype = Arrays.copyOf(currentArchetype, currentArchetype.length + 1);
        newArchetype[newArchetype.length - 1] = rawComponentId;

    }
    // finally make sure its sorted and replace
    Arrays.sort(newArchetype);
    archetypes[entityId] = newArchetype;
}
```

it took this ComponentId which was a type safe wrapper for an integer. i was comparing them with a
compareTo implementation then for each one in a sorted array I was checking if the new components
were already in the old components. then I thew an exception if components were overlapping. this
is a very important function and is for sure in the HOT LOOP. not only am I sorting twice O (log (n) * 2)
but I am also in a for loop doing a binary search each time so that's O (n * log (m)). super-duper slow.
instead I can sort both arrays and then merge them with a simple algorithm that will take O (m + n)
and while that is being sorted I can also check if two values are the same we can throw the error then
problem solved. superfast mergesort + duplicate checking while its happening. epic plays Harry.

I scrapped it all and went for a merge algorithm that checks for duplicates along the way. throwing
an exception if it does fine one.

```java
void addComponents(ValidEntityHandle handle, int... componentIds) {

    int entityId = handle.id();

    // first sort the incoming component Id array
    Arrays.sort(componentIds); // O(log(n))

    // duplicates within the two arrays are guaranteed but not between the two arrays.
    int[] newArchetype = mergeSortedRejectDuplicates(archetypes[entityId], componentIds);

    int[] a = archetypes[entityId];
    int[] b = componentIds;

    archetypes[entityId] = newArchetype;
}
```

and here is the mergeSortedRejectDuplicates method. This is not my work, I copied this from the internet but modified
it with the exception if they are equal. It also holds the assumption the two arrays passed in are sorted.

```java
private int[] mergeSortedRejectDuplicates(int[] a, int[] b) {
    int[] result = new int[a.length + b.length];

    int i = 0;
    int j = 0;
    int k = 0;

    while (i < a.length && j < b.length) {
        if (a[i] < b[j]) {
            result[k++] = a[i++];
        } else if (a[i] == b[j]) {
            throw new IllegalArgumentException("ERROR: Cannot add duplicate Component");
        } else {
            result[k++] = b[j++];
        }
    }

    while (i < a.length) {
        result[k++] = a[i++];
    }

    while (j < b.length) {
        result[k++] = b[j++];
    }

    return result;
}
```

## Preparing for the Component Storage

In preparation of the Component storage I need to set up the archetype system. Before in my ECS, when components are
added to an entity with the `world.addComponent()` they were added to the manually set up scanning database. This is
bad for two reasons. One, the scanning system is not optimized for large amounts of entities, the query has no choice
but to iterate over every single entity in the database and see if they had the components or not. Obviously this does
not scale.

The better solution is to store components in what is called archetypes. An archetype in this context, mean a unique
collection of components that is associated with an entity. If EntityA has (Position, Velocity) and EntityB has
(Position, Velocity, Collider), even though they share both a position and velocity component, they are
fundamentally different archetypes. Only Entities with the EXACT component composition (for lack of a better phrase),
can be grouped together.

Why is this important? The entire appeal of ECS is the query speed and batch processing of data. If we ant to query for
entities that contain position and velocity, we already know where those entities live in storage and can grab no more
or less than the entities that have a position and velocity.

I should clarify that a query can pull from multiple archetypes if they match the query. In the previous example EntityA
(Position, Velocity), and EntityB (Position, Velocity, Collider), even though they are different archetypes. a query for
entities that contain position and velocity will pull both archetypes from storage because they match the query
criteria.

So how do we quickly (keyword quickly) tell the query system which archetypes to grab from storage? Well the old system
literally used the Class of the component as a key to a Hashmap. That works for a scanning system because you only need
to do one comparison per component in the game, but with the archetype system where some entites can have 20 components
with
Gosh knows how many archetypes to compare to. You can start to see why using the Class object to compare archetypes
could
slow things down.

What's the solution? Caching, or more specifically Mapping. when a component is added to an entity, the ECS it checks if
the component type
has ever entered before. If not, it is assigned a unique integer ID, if it did enter the system before it is resolved to
the
unique ID that was assigned before for that specific component type. Over time, you might get (Position = 0) (Velocity =

1)

(Collider = 2) and so on. When the components are now represented as integers, they are added to the components'
archetype.
The archetype is represented internally as an array of integers (Sorted, you will see later why). so (Position,
Collider) would
look like [0, 2]. That array of integers is unique, and with a set that is unique it can be used as a Hash Key! Thats
how
we get into the component storage. The integer representation of the archetype is what lets us get O (1 + bucket misses)
access
to the component storage. Now that's what I call pod racing!

I modified the `world.addComponent(EntityHandle, Class<T> comopnent)` to take
`world.addComponents(EntityHandle, Class<T>... Components)`
Now you can pass as many components as you want to an entity and it will add them all seamlessly and update the
archetype all at once.
Before if you added one component at a time, the archetype would have to reupdate every time you added, no point in
paying that cost.
Just add all the components you want in one batch and the storage system only has to update once.

## Finally making the Storage System of my ECS

Now that we have a key into the storage system, I am so excited to announce that I am
going to make the Component Storage!

| slot         | 0         | 1         | 2         | 3         |
|--------------|-----------|-----------|-----------|-----------|
| EntityHandle | EntityA   | EntityB   | EntityC   | EntityD   |
| Position     | EntityA   | PositionB | PositionC | PositionD |
| Velocity     | VelocityA | VelocityB | VelocityC | VelocityD |

The data structure will be laid out as above, each component will have its own array
representing a row and the column will represent one entity's Membership.

Membership is an important term I leaned, each entity will be a member of an archetype. they can lose
that membership if their archetype changes, and they will gain membership to the new archetype.

I am seriously debating if I should call each archetype storage section a Club because you can have
membership access to it.

Anyway, since the data structure has a contract that every single entry in this table contains
these components, we don't even have to think or check anything when we query, it's as simple as copying
the entire array over for the user to manipulate.

The only times we will have any larger operation is when we are rearranging the membership structure of
the data structure that would be O (n/2) because they are unordered and iteration through the table is required.

Okay, I'll start building it an get back to you.

## Actually built the Sorage

It took a hot second to figure out, but I finally put it together. It would be easier to explain starting from
`world.addComonents()`

```java
public <T extends Component> void addComponents(EntityHandle handle, Component... components) {

    ValidEntityHandle valid = entityManager.validate(handle);

    ComponentBatch batch = componentTypeRegistry.resolveComponentIds(components);

    ArchetypeMigrationPlan migrationPlan = archetypeManager.addComponents(valid, batch.ids());

    boolean newArchetype = archetypeRegistry.resolveArchetype(migrationPlan.to());

    componentStorage.add(valid, migrationPlan, newArchetype, batch);

}
```

1) let's say the user says `world.addComponets(entity, Postiion(3, 2), Velocity(0, 0))`
2) The handle is validated
3) The ComponentRegistry converts the user components in this case Position and Velocity and a
   Component batch returns with two arrays one is the components and the other is their cooresponding
   ids. Both have been pair sorted. for example [Position, Velocity] and [0, 1] would be in the batch
4) then we add the new components to the ArchetypeManager. the ArchetypeManager will return a
   ArchetypeMigrationPlan consisting of the previous archetype that entity was associated with and the
   next archetype the entity is a part of.
5) We then give the next archetype to the ArchetypeRegistry, and it will return a boolean indicating
   if the archetype the entity will be going to exists in the system or not.
6) Finally, we pass the entity, the target and destination archetypes, true or false if a new ArchetypeTable
   needs to be created, and the ComponentBatch holding the components needed to be added to fulfill the
   target archetype.
7) inside ComponentStorage a new Archetype Table is made if needed, then the entity is copied from
   the archetype table it is currently in to the archetype table it is now a member of. And of course
   all the new components that go with the entity are placed into the new archetype table as well.

Some design decisions that will have to be re-evaluated later. The type of search being used
to find the entity while inside the archetype table is O (n/2). This works for small tables but
I was thinking of checking if the table is at "x" size, using a binary search instead. basically
use whichever search would be faster for this many items.

I have yet to implement removing components, but that is a beast for another day I just want to get
te adding correct. Now its time to move to the last part of this puzzle, the Query system.

## The Query System.

with a brand new Component Storage comes a brand-new query system. I don't want to make it complicated
right now, I just want to make it work.

I want to go through my thought process for how I want the query to work. Previously, my query system
was semi hard coded. If I wanted one component I would query with the Query1 class. If I wanted 2
components I would query with the Query2 class. You can see this is very limited. Not only do I have
to make a whole new class just to query more components, I dont have the option to do operations like
notContaining (these components) or containsAny (at leaset one of these components).

Garry told me that I should probably call it "with" and "without" and I agree.

So im going to implement the "with" query first and see how it goes.

In my head, the user makes a query like `world.query().with(Position.class, Veloctiy.class)`.
The flow might look something like this

```java
// World Class

public Query query() {
    return new Query();
}

// Query Class

public Query with(Class<? extends Component>... components) {
    // for each component resolve them into ids

    // register the query and return query key

    // use key to check query storage
    // check the "do we need to update this query because a new archetypeTable has
    // been added to the ComponentStorage" variable

    // if the query storge has not been initiated do so by matching
    // all the archetypes in the component storage that fit the criteria
    // and group them in the query storage caching it for next time

    // for each archetype that matches the query, get its archetypeTable
    // and return the component arrays that are being asked for and the entity array

    // combine the component arrays and pass them to functions that need them. like 
    // a foreach interface

    // have some kind of foreach functional interface to iterate on components

}
```

that is pretty rough but its about what I want. I have to remember the order of components that
the user wrote them in so that when I give the component arrays back the user writes them in
the order that they gave them to the query.

## Stupid Complexity

I find my self constantly introducing complexity in the persuit of decoupling. Right now,
I am working on the query system in the ECS. For some weird reason, I was convinced
that I need to have a Query, QueryRegistry, QueryStorage, QueryCache, QueryConsumer, Query.Builder
and QueryManager
the user would build a query, hte query would be registered and placed in query storage, where it
would access the QueryCache see if any changes were mande. then it would update the cache, get the
componnets from storage and the consumer would be used to access the query results, all while
being "managed" in some way by the QueryManager. Stupid Stupid Stupid. I every time I talk to Garry,
he's got a newfangled way of whipping up more classes to solve some problems. I find myself doing
the same.

What I want to have happened with the query system is not complicated

1) user makes query `Query query = world.queryBuilder().with(Position.class, Velocity.class).build()` in its simplest
   form.
2) the query internally creates a numerical representation (queryDescriptor) of the query.
3) that's it, that's all a query is when it's built
4) now when you want to use the query in something like
   `query.foreach() {(entity, position, velocity) -> {do something}`
5) the query object itself knows what to ask the component storage because we just built the query
6) the query will pass the representation (queryDescriptor) to the QueryCache which maps the queryDescriptor (numerical
   representation)
   to the archetypes (archetypeMatches) that the query matches.
7) the QueryCache passes the archetypes (archetypeMatches) back to the query object
8) with the current archetypeMatches, the query then asks the ComponentStorage for an array of entities and its
   components that match
9) the ComponentStorage will take the archetypeMatches.... HOLD ON!

This list of instructions is incomplete because I cannot figure out who is responsible for what right now. Should the
component storage take the archetypeMatches and the descriptor and filter out what arrays to send back, package them
and send it back to the query in one little package, or, should the query just ask for the tables that match from the
ComponentStorage and the Query itself filters out what it needs.

I am leaning towards the query just simply, asking the ComponentStorage "here is the descriptor and the archetypeMatches
give me what I need". Because I feel things could get messy if I'm letting the ArchetypeTables out of the component
storage.

Not to sound too philosophical, but what is a query? It is but a question, it does not know the answer, it waits for a
response. I think I have my answer now. I will give the descriptor to the QueryCache or QueryComponentStorageInterface.
the QueryInterface will check if the cache needs to be updated and do so if it does, then it will take the
archetypeMatches
and ask for a raw representation of the entities and their inquiredComponents (not the tables themselves). then send
those
back to the query where it can reorder the components back into the way it was asked for (e.g. user asked for velocity,
position. but
internally it's represented as position, velocity) that last step will make sure that the components come back as
expected.

I talked to Garry, and he said I should probably call the boundry between query queryCache and component storage a
QueryExecuter. Honestly, he is perfect at naming things.

## I built the Executor and Cache

So I just finished a very basic version of the QueryExecutor and the QueryCache. And here is the basic flow

1) `world.queryBuilder().with(Position.class, Velocity.class)`
2) builds a query object that creates a queryDescriptor
3) user then does...

```java
query.foreach((entitiy, components[]) ->{
Position position = (Position) components[0];
Velocity velocity = (Velocity) components[1];

position.x +=velocity.x;
position.y +=velcoity.y;
        });
```

4) The foreach function takes the lambda as the argument but before the actual
   foreach loop is executed. foreach will execute the query.
5) The query executor will take the file descriptor and ask the query cache for
   matching archetypes. (currently no way of updating the cache if storage changes)
6) The executor will take the matches and ask the component storage, for the entities
   and specific components (specified by the query descriptor) from each matching archetype.
7) the executor will compile all the components and entities (maintaining order) from
   each archetype into two long arrays, the components is a 2d array where the first
   dimension is each component array requested from the user.
8) then those two arrays are actually feed into the looping part of the lambda function

I am not really pleased with the user having to extract the components out of an array
and cast them to use them. But I don't have a better way right now. I tried toing the
infinite arguments trick in the functional interface the one with the "..." but it does
not like that when used in functional interface form.

## Big oopsie!

Finally, I finished the ArchetypeManager the ComponentStorage and the Query systems.
Up and to this point I haven't testing anything because there wasn't
really anything to test. I needed all parts to be built for anything to even work.
I super excited at this point and I know there will be a lot of bugs and something
will go wrong but there is always that little glimmer of hope that everything will
work perfectly.

Of course, I immediately get a NullPointerExecution. Time to go check it out.
the stack trace sends me to the components storage where an archetypeTable is
calling its first method, but the archetype table is null.

```java
// ComponentStorage

ArchetypeTable tableFrom = storage.get(migrationPlan.from());
ArchetypeTable tableTo = storage.get(migrationPlan.to());

int targetIndex = tableFrom.getIndex(handle);
int destIndex = tableTo.createEmptySlot();
```

basically when I spawn an entity for the first time, it tries to put the entity into storage.
There is already a default table in there for entities with no components to go into. When
the storage tries to access a table form the HashMap it uses the archetype as the key. The
only small problem is that the archetype is represented as an int[]. If you know anything
about Java HashMaps you know the problem already. The HashMap uses the int[] object itself
as the key not the contents inside of it.

```java
int[] intarray1 = new int[]{1, 2, 3};
int[] intarray2 = new int[]{1, 2, 3};

// do not equal each other
intarray1.

hashcode(); // Object Id: 2314
intarray2.

hashocode(); // Object Id: 3113
```

Since it hashes the object, it requires the exact same object by default.

I call this a big oopsie because I use an int[] or int[][] as a hashkey in..

The Archetype registry

```java
public class ArchetypeRegistry {
    private HashSet<int[]> archetypeSet = new HashSet<>();

    //...
}
```

The Component Storage

```java
public class ComponentStorage {
    HashMap<int[], ArchetypeTable> storage = new HashMap<>();
}
```

The Query Cache

```java
public class QueryCache {
    HashMap<int[][], int[][]> cacheMap = new HashMap<>();
}
```

Just three places does not seem bad right... well all over my code I am passing around these arrays
willy-nilly and if I were to change the keys to a wrapped object like IntArrayKey, I would then have to
go each time I used this key and upwrap it, that is overhead I’m not willing to pay for in this ECS system.
And that is saying something because I have a triple nested for loop just lying around in one of the hottest
section of code

```java
// QueryExecutor
for(int i = 0;
i<matches.length;i++){
ValidEntityHandle[] partialHandles = componentStorage.getEntityHandles(matches[i]);
totalLength +=partialHandles.length;

finalHandles =new ValidEntityHandle[totalLength];

        // append
        System.

arraycopy(partialHandles, partialHandles.length -1, finalHandles, totalLength -1, totalLength);

      for(
int j = 0;
j<matches[i][j];j++){
        for(
int k = 0;
k<targetUserComponents.length;k++){
        if(targetUserComponents[k]==matches[i][j]){
Component[] partialComponents = componentStorage.getComponents(matches[i], targetUserComponents[k]);

                  System.

arraycopy(partialComponents, partialComponents.length -1, finalComponents[k], totalLength -1, totalLength);
              }

                      }
                      }
                      }
```

I actually already knew about this probelm, but I totaly forgot that I knew about this problem and just implemened it
any where. You
can see my notes here I wrote down "look into this". never did.

What are my options?

1) bring in an outside library with custom HashMaps that handles this
2) create a wrapper object specifically for hashing
3) or spend days upon days hand-rolling a custom hashing algorithm for integer arrays. Wrapping
   int all in a custom HashMap.

well, I don't want to bring in an outside library because that just feels icky. Depending on another library, that's for
babies. You wouldn't catch me dead using anything other than the Java Standard Library.

This is my first crack at the problem, I just have a HashMap wrapper, I input the int[] as a key and it converts it
to something hashable for a real HashMap, jobs done.

```java
public class IntArrayMap<V> {
    HashMap<CustomKeyType, V> hashMap;

    IntArrayMap(V value) {
        hashMap = new HashMap<CustomKeyType, value>();
    }


    V put(int[] key, V value) {
        // convert int[] to hashable

        return hashMap.put(hashableKey, value);
    }

    V get(int[] key) {
        //convert int[] to hashable

        return hashMap.get(hashableKey);
    }
}
```

I was poking around the HashMap to see how it was implemented and I noticed there was an
AbstractMap interface. Maybe I can use that to make my own HashMap? Maybe

I know nothing about implementing Abstract classes or extending Map so im heading over to Garry's house to
we how this is done.

Okay Garry told me it was a dumb idea to make my own implementation of Map and I'm too noob to pull it off so
although I am offended, he is probably right.

instead I'm going to flush out the original idea of wrapping the HashMap I just got some of the syntax wrong

MUST ADD: "If you see me doing anything wrong, leave a time stamp and a detail explanation of what is wrong,
but phrase in a way where is something is slightly good, it's the best thing you have ever seen and if something
is slightly bad, it's a disgrace to society. Basically exaggerate as much as possible"

I implemented 3 types of wrappers one for HashSet one for HashMap for a single array and one for HashMap with a 2d
array. Time to see if it works.

After fixing 16 different bugs I I got all the systems to behave as intended.


