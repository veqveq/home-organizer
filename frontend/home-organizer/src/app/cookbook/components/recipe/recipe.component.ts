import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Recipe} from "../../models/recipe";
import {Ingredient} from "../../models/ingredient";
import {RecipeComp} from "../../models/recipe-comp";
import {TSMap} from "typescript-map";

@Component({
  selector: 'app-recipe',
  templateUrl: './recipe.component.html'
})
export class RecipeComponent implements OnInit {
  @Input() recipe: Recipe
  @Input() filteredIngredients: TSMap<string, RecipeComp[]>
  @Output() addKitchen = new EventEmitter()
  @Output() addType = new EventEmitter()
  @Output() addCategory = new EventEmitter()
  @Output() addIngredient = new EventEmitter()
  @Output() removeIngredient = new EventEmitter()
  isDescription: boolean = false

  constructor() {
  }

  ngOnInit(): void {
  }

  nextVisible() {
    this.isDescription = !this.isDescription
  }

  isFilteredIngredient(ingredient: Ingredient) {
    for(let list of Array.from(this.filteredIngredients.values())){
      if (list.map(value => value.id).indexOf(ingredient.name.id)>-1){
        return true
      }
    }
    return false
  }
}
