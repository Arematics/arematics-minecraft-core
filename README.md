<p align="center">
    <img src="https://arematics.com/assets/icons/arematics-logo.svg" width="150" title="Arematics Logo" 
    alt="Logo">
</p>

# Disclaimer
<span style="color:#CF0000">This Project is  no longer maintained and contains bugs or parts where you couldn't build it. It's the full new developed
minecraft server system of soulpvp.de based on Spring Boot and PaperSpigot 1.8.8</span>

# Minecraft Core System
Develop Minecraft Plugins the easy way with Spring Boot Data JPA.

## Sub Project Setup
The main class of the plugin should call something with "Boot" at the end. 
Also, it needs to extend Bootstrap for having all system relevant information.

## Minecraft Commands and Listener
Commands must be annotated with @Component and extending CoreCommand Super Constructor is being required defining 
the default command name and more names.    
For the default command message create a method returning a boolean value annotated with @Default expect an CommandSender as parameters

Listener Classes must be annotated with @Component and implementing Listener Interface from Bukkit.

For both commands and listeners there you have now fully Spring Autowired Integrations.

