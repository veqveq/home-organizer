import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Recipe} from "../../models/recipe";
import {Ingredient} from "../../models/ingredient";

@Component({
  selector: 'app-recipe',
  templateUrl: './recipe.component.html'
})
export class RecipeComponent implements OnInit {
  @Input() recipe: Recipe
  @Input() filteredIngredients: string[]
  @Output() addKitchenFilter = new EventEmitter()
  @Output() addTypeFilter = new EventEmitter()
  @Output() addCategoryFilter = new EventEmitter()
  @Output() addIngredientFilter = new EventEmitter()
  isDescription: boolean = false

  constructor() {
  }

  ngOnInit(): void {
  }

  nextVisible(){
    this.isDescription = !this.isDescription
  }

  isFilteredIngredient(ingredient: Ingredient){
    if (this.filteredIngredients){
      return this.filteredIngredients.findIndex(value => value === ingredient.name.id) !== -1
    }
    return false
  }
}
