import { describe, expect, it } from "vitest";
import { getProgressLevel } from "./progressionLevel";

describe("getProgressLevel", () => {
  it.each([
    [0, "FAIBLE"],
    [39, "FAIBLE"],
    [40, "MOYEN"],
    [69, "MOYEN"],
    [70, "BON"],
    [99, "BON"],
    [100, "TERMINE"],
  ] as const)("classe %s%% dans le niveau %s", (value, expected) => {
    expect(getProgressLevel(value)).toBe(expected);
  });
});
