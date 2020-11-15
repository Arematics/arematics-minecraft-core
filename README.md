<p align="center">
    <img src="http://www.arematics.com/images/banner/banner-borderless.png" width="350" title="Arematics Banner" 
    alt="Banner">
</p>

# Minecraft Core System
Develop Minecraft Plugins the easy way with Spring Boot Data JPA.

##Sub Project Setup
The main class of the plugin should call something with "Boot" at the end. 
Also, it needs to extend Bootstrap for having all system relevant information.

## Minecraft Commands and Listener
Commands must be annotated with @Component and extending CoreCommand Super Constructor is being required defining 
the default command name and more names.    
For the default command message create a method returning a boolean value annotated with @Default expect an CommandSender as parameters

Listener Classes must be annotated with @Component and implementing Listener Interface from Bukkit.

For both commands and listeners there you have now fully Spring Autowired Integrations.

