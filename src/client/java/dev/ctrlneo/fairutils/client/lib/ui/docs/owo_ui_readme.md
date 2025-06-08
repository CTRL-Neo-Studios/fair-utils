# OWO UI Readme

I'm writing down this entry here is because although I added some documentations to the official owo-ui docs, it's still missing some stuff that I want to add in later

And also it's a note to self for later me. You'll thank me in a bit.

## Concepts

### Static and Dynamic Components

To make sure that components in the screen are initialized correctly, there must be a distinctive line between how to initialize components that are added in different runtimes of code.

**Static Components** are the UI components that are initially there in your data-driven XML document. I'll be calling the document as a UI Sheet now.

**Dynamic Components** are the UI Components that are not there in your initial UI sheet. It's either instantiated into the sheet after some kind of action or that you had a custom UI component preset that you want to instantiate, like a custom list or something.

For initialization, you should put your bindings code of static components in the `build()` function, and the dynamic components in the `init()` function.