// Zero args
Command.Register("listplayers", function (sender)
    sender.SendMessage("List of players: ...")
end)

// One arg
Command.Register("hello", Args.String("name"), function (sender, args)
    local name = args.GetString("name") // Alias for .Get("name", Args.String), but since it's built-in, we provide a shorthand
    // execution
end)
//
//// One optional arg
//Command.Register("fly", Args.Player("target", { optional = true }), function (sender, args)
//    local target = args.GetPlayer("target") // Same shorthand as above
//    // execution
//end)

// Literal union
Command.Register("randomfood", function(sender)
    local food = GetRandomFood()
    sender.SendMessage("Here's a completely random food: " .. food)
end)
Command.Register("randomfood", "fruit", function(sender)
    local fruit = GetRandomFruit()
    sender.SendMessage("Here's a random yummy fruit: " .. fruit)
end)
//Command.Register("randomfood", "vegetable", Args.Boolean("yummy", { optional = true }), function(sender, args)
//    local isYummy = args.GetBoolean("yummy") or false
//    // logic
//end)

// Permission
//Command.Register("op-everyone", { permission = "custom.wtf" }, function(sender)
//    // op everyone!!!
//end)

// Command.Register("countdown", Args.Integer("min", { min = 1 })