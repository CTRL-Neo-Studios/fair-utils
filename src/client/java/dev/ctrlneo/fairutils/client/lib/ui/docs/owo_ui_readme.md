# OWO UI Readme

I'm writing down this entry here is because although I added some documentations to the official owo-ui docs, it's still missing some stuff that I want to add in later

And also it's a note to self for later me. You'll thank me in a bit.

## Concepts

### Static and Dynamic Components

To make sure that components in the screen are initialized correctly, there must be a distinctive line between how to initialize components that are added in different runtimes of code.

**Static Components** are the UI components that are initially there in your data-driven XML document. I'll be calling the document as a UI Sheet now.

**Dynamic Components** are the UI Components that are not there in your initial UI sheet. It's either instantiated into the sheet after some kind of action or that you had a custom UI component preset that you want to instantiate, like a custom list or something.

For initialization, you should put your bindings code of static components in the `build()` function, and the dynamic components in the `init()` function.

### Static & Dynamic Components Runtime

After initializing **Static Components** and **Dynamic Components** in your UI, you might want to make some of those elements real-time, meaning it would update its content in realtime in accordance to some data you have.

For this, you'd have to do the following:
1. During the Static/Dynamic Initialization, store the reference of the component in an attribute.
2. ... still figuring out how to make it realtime...

### Container Sizing

If you're using Flow Containers, then if you're trying to make the container fit with the content size... you don't really have to do anything. A flow container will stretch and wrap itself around its children content. It is by default. Can't believe it took me a long while to figure that out...

For the sizing **`expand`** property (set at 100%), using it in a flex container is like having a div being put on a `flex-grow` class. It will resize the container to grow to the container's width/height. The `fill` property is setting the width/height of the set container to the percentage of width/height of the parent container.
