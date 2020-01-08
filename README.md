# Pokemon Utilities

I got tired of looking things up for type matchups and trying to plan out
assigning pokemon to jobs so I started writing some dumb little utilities


## Type Matchups

The intention of this utility is to pop in whatever expected attack types or
expected defending pokemon types and find the type or type-combination with the
best attack modifier.

### Done

- best attacker against single-type defender
- best single-type defender against attacking type

### TODO

- best attacker against dual-type defender
- best dual-type defender against attacking type
- overall best defender (how to score this?)
- CLI interface


## Job Assignment

The intention of this utility is to provide a list of jobs with expected type
and number of slots, and provide an assignment of pokemon that will maximize
bonus exp from type matching but also fill the most slots possible.

### Done

- categorize pokemon based on available jobs

### TODO

- populate jobs
- read input of jobs available (user manually generated)
- read input of pokemon in boxes (user manually generated)

### Maybe TODO?

- CSV of all jobs so user input just list of names of jobs


## PokeDex

Haven't started yet. I'd like to just pop in a list of Pokemon names and have a
single PokeDex utility to get extra information about those pokemon
