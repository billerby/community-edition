/**
 * Admin Console Application Tool component
 */

function main()
{
   model.themes = [];
   
   // retrieve the available theme objects
   var themes = sitedata.getObjects("theme");
   for (var i=0, t; i<themes.length; i++)
   {
      t = themes[i];
      model.themes.push(
      {
         id: t.id,
         title: (t.titleId != null && msg.get(t.titleId) != t.titleId ? msg.get(t.titleId) : t.title),
         // current theme ID is in the default model for a script
         selected: (t.id == theme)
      });
   }
   
   // logo image override
   model.logo = context.getSiteConfiguration().getProperty("logo");
   
   // Widget instantiation metadata...
   var defaultlogo = msg.get("header.logo");
   if (defaultlogo == "header.logo")
   {
      defaultlogo = "app-logo.png";
   }
   
   var widget = {
      id: "ConsoleApplication", 
      name: "Alfresco.ConsoleApplication",
      options: {
         defaultlogo: url.context + "/res/themes/" + theme + "/images/" + defaultlogo
      }
   };
   model.widgets = [widget];
}

main();