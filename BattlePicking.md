# How to pick battles when picking up a story

wiki.c2.com is where a lot of discussion happened 

"Howard G. Cunningham is an American computer programmer, who developed the first wiki and co-authored the Manifesto
for Agile Software Development"

Developers created agile, not business people. If we don't understand its aim, then we cannot tell if it is a success.
Personally, for our mental health we need to pay a lot more attention to

https://agilemanifesto.org/principles.html

"Agile processes promote sustainable development.
The sponsors, developers, and users should be able
to maintain a constant pace indefinitely."

Writing tests is part of that. If the way we write tests is taking up more and more energy, the only path is burn out.
This is why arguments about using a fixed testing strategy are pointless. A testing strategy, for example, doing end 
to end only for a 1000-line project may be doable. But for a 100,000+ line project, I would not trust those tests. 
The tests end up more complicated than the code, and I have encountered enough bugs in tests passing for the wrong 
reasons to have faith in them. We should not be relying on hope in the test suite. Relying on hope is one of the last
things we should rely upon when engineering anything. This is also why using AI to write tests is following the rules
and not playing the game. It turns it into a box-ticking exercise, a machine cannot determine how the next human can
interpret and rely on it.

Tests were started to help us go at a stable speed as the complexity grows and the software changes hands, if they are
not achieving that, then they are not achieving their aim.

So we organise tests around coherence. How well they can explain the weirdness that can come with time and
requirements that likely conflict with previous requirements. Businesses do not keep track of what they have asked
for. So have no idea what can and cannot be done, and whether something can or cannot be done is often at the mercy of
how we have done things.

## Technical Debt
Technical Debt is often viewed as an us/dev problem, versus a business problem. How we organise affects what we can
produce and whether things are achievable. So Technical Debt is a business problem. People just gamble it won't ever
be a "them" problem and kick the can down the road. 

When pushed to ignore technical debt, it is advisable to make note of who benefits from the corner cutting. Quite
often people are very happy to leave others big problems, and likely not even be able to perceive those problems
until they happen. Reactive versus proactive mind set. An environment that has fallen into being highly reactive 
means the business has fewer choices going forward in that area.

Technical debt acts like resistance in an electrical system. High debt equals high resistance. Eventually you reach
the point where things just become unviable, and fixing those problems can require a much higher skill set than not
doing it in the first place. The concept of "We will do it later" falls apart when only a few can do it, often not
the person who said "We will do it later".

## Figure out how effort can be spent when doing the story before doing any code changes
When the story is first picked up, we should look at what changes we can make, including tests to decide what path to
take. Often people do what they deem the easiest code change, leaving a much more complicated test change potentially
leading to meandering incoherence.

Tests are how we communicate our intentions to each other. They are not about proving our expectations to ourselves.
Many a time I have found code that makes little sense and does not have a test to prove that intention, this makes me
doubt the ability and reduces trust in the original author. People often have a wide range of what they deem important
and unimportant to test. Often they just look for coverage and are very "happy path" minded. Many tests are written by
people who don't understand why they do it. They see it as proving themselves correct, but in reality it is about
creating an environment that can change safely and with minimum stress to new requirements. Tests can be a boat anchor
to productivity, massively increasing the requirement of energy if we don't monitor how we work. We pay that price,
not the business. 

## Poor code organisation leads to a lot of code conflict issues.
Martin Fowler's book Refactoring (Java edition) is as much about writing code that will need less refactoring a
refactoring code. It highlights why the code needs refactoring and the fix. If you know the cause, we can avoid the
cause and create code that churns less.

### Lots of small classes scale better across people than lots of large classes.
It really comes down to probability. If you have 10 people working in a project, and there are 10 classes, people will
be tripping over each other slowing each other down. Worse, to remedy the slow-down, more people will be added to the 
project. This further decreases individual velocity, increasing the energy we need to achieve things. We have limited
energy, so we need to be mindful of how our choices affect this for each other. "to maintain a constant pace
indefinitely" is a statement that ties into this. Constant pace indefinitely means constant energy requirement. A
3-point story at the start should not be an 8-point story later. That is a sign of an environment that is getting
hostile to work in. Simple has become hard, hard may have become impossible for many. This bottlenecks who can now
actually do the work safely.

### Incoherence should not be the normal state
Simple/Readable have now been reduced to words that have little quantification and little value. Simple often means
the least effort to write, not simple when trying to get a holistic understanding of the system. We should strive to
make the system as easy to understand as possible with as few mental steps as possible. Coherence is measured by the
mental journey we need to go on. The fewer mental steps, the less misdirection, the better we can make judgement calls
safely. 

#### Language features are there to help us manage coherence and our sanity
Features are usually not put into programming languages for reasons of fun. In reality, they are there to help with
incoherence. Removing the need for a lot of mental gymnastics. For example, pattern matching/ADTs are not in a lot of
languages. They save a lot of headaches and help communicate things more clearly. Some people's brains naturally work
associatively, so not having them just adds needless indirection for them. Intelligence is not a linear scale, more
often it is to do with people's brain gravitating in different directions. Teaching people to design around
associations tends to help with understanding and memory. Cleaning up a project can rely a lot more on its more
advanced language features. 

Without certain types of abstractions, things can never really be made clear, leading to a low-complexity ceiling
before people start looking for a different project. How do you communicate the symetry in requirements in a language
that has problems communicating symetry? We need to be careful that our definition of simple is not just a path to
unpleasant incoherence. Simple and lazy are often an unfortunate synonym. 

#### Metacognition/Meta-thinking
By learning to monitor our own thought processes, we can improve how we work and help how others work. Whenever I
pick up a story, I look to see how I can do the story and reduce the cognitive load in the current implementation. The implementation in a microservice should be chrystal clear. If we cannot manage that in a microservice, then
there will be major problems in anything larger. Engineering practices really come into play on bigger things, and we
should not rely on working on bigger things to get good on bigger things. We use the smaller things as training
environments, so we are prepared and can leave the people after us a nice experience. We can only expect to inherit
what we leave others, anything else is hypocritical. Would you train for a marathon by running the marathon?

Many others feel the corners we cut repeatedly. So don't let the Nirvana Fallacy "Don't let perfect be the enemy of
good" actually be "Penny wise, pound foolish". In software engineering there is often no such thing as perfect. Just
it costs too much energy to do. By self-education, we can learn how to apply energy better, and we can turn perfect
into good. Misapplied Nirvana Fallacy sadly seems to be a major cause of poor software projects to work in.

#### Cyclomatic complexity
Incoherence is often tied to high cyclomatic complexity. This is one of the main things we can use to determine where
we put out changes and how those changes will affect the current tests. If we put a branching at the top of the call
chain, we have heavily impacted the design. This is often a sign that we are breaking the single responsibility
principle.


## Help
### Feature envy
https://wiki.c2.com/?FeatureEnvySmell
"The whole point of objects is that they are a technique to package data with the processes used on that data. A
classic [code] smell is a method that seems more interested in a class other than the one it is in. The most common
focus of the envy is the data."
"

This is incredibly simple to resolve. We just move the logic onto where the data is.


```scala



```