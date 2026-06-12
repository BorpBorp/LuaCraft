local describe, test, expect = Test.describe, Test.it, Test.expect

describe("example", function()
    describe("module1", function()
        test("feature1", function()
            local item = Item.New("stone", 44)
            local itemStack = TestReflect.GetField(item, "stack")

            local amount = TestReflect.CallMethod(itemStack, "getAmount")
            expect.equal(amount, 44)
        end)
    end)
end)