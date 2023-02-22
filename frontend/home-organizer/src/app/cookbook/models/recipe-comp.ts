import {OnNamed} from "../../core/models/onNamed";
import {OnEqual} from "../../core/models/onEqual";
import {GroupIngredientName} from "./group-ingredient-name";

export class RecipeComp extends OnEqual implements OnNamed{
  id: string;
  name: string;
  hidden: boolean = false;
  group: GroupIngredientName;

  constructor(id: string, name: string, group?: GroupIngredientName) {
    super()
    this.id = id;
    this.name = name;
    this.group = group
  }

  protected onEqual(obj: RecipeComp): boolean {
    return this.id === obj.id && this.name === obj.name;
  }
}
