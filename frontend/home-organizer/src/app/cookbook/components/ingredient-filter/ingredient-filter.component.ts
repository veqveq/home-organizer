import {Component, ElementRef, EventEmitter, HostListener, Input, OnInit, Output, ViewChild} from '@angular/core';
import {RecipeCompFilter} from "../../models/recipe-comp-filter";
import {RecipeComp} from "../../models/recipe-comp";
import {RecipeCompService} from "../../services/recipe-comp.service";
import {BehaviorSubject, map, Observable, tap} from "rxjs";
import {GroupIngredientName} from "../../models/group-ingredient-name";
import {TSMap} from "typescript-map";

@Component({
  selector: 'app-ingredient-filter',
  templateUrl: './ingredient-filter.component.html'
})
export class IngredientFilterComponent implements OnInit {
  @ViewChild('inputField') inputField: ElementRef
  @ViewChild('menu') menu: ElementRef
  @ViewChild('menuList') menuList: ElementRef
  @Input() title: string
  @Input() endpoint: string
  @Output() filterComponents = new EventEmitter();

  pickedValues: TSMap<string, RecipeComp[]> = new TSMap<string, RecipeComp[]>()

  filter: RecipeCompFilter = new class implements RecipeCompFilter {
    name: string;
  }
  curElementIndex: number = 0
  page: number = 0
  totalPages: number

  showList = false
  groups: GroupIngredientName[] = []
  nameIngredients: RecipeComp[] = []

  constructor(
    private eRef: ElementRef,
    private service: RecipeCompService,
  ) {
  }

  @HostListener('document:click', ['$event'])
  clickout(event) {
    if (!this.eRef.nativeElement.contains(event.target)) {
      if (this.showList) {
        this.closeMenu()
      }
    }
  }

  pickIngredient(ingredient: RecipeComp, group?: GroupIngredientName, doEmit: boolean = true) {
    ingredient.hidden = true;
    let groupId
    if (!group) {
      groupId = ingredient.groupId ? ingredient.groupId : ingredient.id
    }else{
      groupId = group.id
    }
    let pickedIngredientsFromGroup = this.pickedValues.get(groupId) as RecipeComp[];
    if (!pickedIngredientsFromGroup) {
      pickedIngredientsFromGroup = []
    }
    pickedIngredientsFromGroup.push(ingredient);

    this.pickedValues.set(groupId, pickedIngredientsFromGroup)
    if (group){
      if (this.getNotPickedFromGroupCount(group) == 0) {
        group.hidden = true
      }
    }
    if (doEmit){
      this.filterComponents.emit(this.pickedValues)
    }
  }

  pickGroup(group: GroupIngredientName) {
    group.hidden = true
    if (this.getNotPickedFromGroupCount(group) > 0) {
      group.childIngredientNames
        .filter(ingredient => !ingredient.hidden)
        .forEach(ingredient => this.pickIngredient(ingredient, group,false))
    } else {
      this.pickedValues.set(group.id, [new RecipeComp(group.id, group.name)])
    }
    this.filterComponents.emit(this.pickedValues)
    if(this.groups.length == 0){
      this.closeMenu()
    }
  }

  getNotPickedFromGroupCount(group: GroupIngredientName) {
    return group.childIngredientNames.filter(ingredient => !ingredient.hidden).length
  }

  getTotalPickedValuesCount(){
    let counter = 0
    this.pickedValues.forEach((value) => counter+=value.length)
    return counter
  }



  returnIngredient(ingredient: RecipeComp) {
    let groupId = ingredient.groupId?ingredient.groupId:ingredient.id
    let valuesList = this.pickedValues.get(groupId).filter(value => value !== ingredient)
    ingredient.hidden = false
    let group = this.groups.find(group=>group.id === groupId)
    if (group){
      group.hidden = false
    }
    if (valuesList.length == 0) {
      this.pickedValues.delete(groupId)
    } else {
      this.pickedValues.set(groupId, valuesList)
    }
  }

  ngOnInit(): void {
    this.doFilter()
    addEventListener('keyup', ev => {
      if (this.showList) {
        let menu = this.menuList.nativeElement
        if (ev.key == 'ArrowDown') {
          this.curElementIndex++
          menu.scrollTop = menu.scrollTop + 40
          if (this.curElementIndex == this.groups.length) {
            this.curElementIndex = this.groups.length - 1
            this.doFilterNextPage()
          }
        } else if (ev.key == 'ArrowUp') {
          this.curElementIndex--
          menu.scrollTop = menu.scrollTop - 40
          if (this.curElementIndex < 0) {
            this.curElementIndex = 0
          }
        } else if (ev.key == 'Enter') {
          this.pickGroup(this.groups[this.curElementIndex])
        }
      }
    })
  }

  doFilter() {
    this.service.doFilter(this.filter, this.endpoint, 0)
      .pipe(
        tap(resp => {
          if (!resp.last) {
            this.page = resp.number + 1
            this.totalPages = resp.totalPages
          }
        }),
        map((resp) => resp.content)
      )
      .subscribe(items => this.groups = this.handleRecivedItems(items))
  }

  doFilterNextPage() {
    if (this.page < this.totalPages - 1) {
      this.service.doFilter(this.filter, this.endpoint, this.page)
        .pipe(
          tap(resp => {
            if (!resp.last) {
              this.page = resp.number + 1
            }
          }),
          map((resp) => resp.content)
        )
        .subscribe(items => this.groups.push(...this.handleRecivedItems(items)))
    }
  }

  handleRecivedItems(nameGroups: GroupIngredientName[]) {
    nameGroups.forEach(group => {
      if (group.childIngredientNames.length > 0) {
        group.childIngredientNames.splice(0, 0, new RecipeComp(group.id, group.name))
      }
      let existPickedIngredients = this.pickedValues.get(group.id)
      if (existPickedIngredients){
        let existGroupIngredientIds = existPickedIngredients.map(value => value.id)

        group.childIngredientNames.forEach(value => {
          if (existGroupIngredientIds.includes(value.id)){
            value.hidden = true
          }
        })
      }
      if(group.childIngredientNames.length){
        group.hidden = this.getNotPickedFromGroupCount(group) <= 0
      }else{
        group.hidden = this.pickedValues.has(group.id)
      }
    })
    return nameGroups;
  }

  reset() {
    this.inputField.nativeElement.value = ''
    this.pickedValues.clear()
    this.filterComponents.emit(this.pickedValues)
    this.doFilter()
  }

  openMenu() {
    this.showList = true
    let dropdownContent = this.menu.nativeElement
    dropdownContent.classList.add('show');
    dropdownContent.style.height = 'auto';
    var height = dropdownContent.clientHeight + 'px';
    dropdownContent.style.height = '0px';
    setTimeout(function () {
      dropdownContent.style.height = height;
    }, 0);
  }

  closeMenu() {
    this.showList = false
    this.curElementIndex = 0
    let dropdownContent = this.menu.nativeElement
    dropdownContent.style.height = '0px';
    dropdownContent.addEventListener('transitionend',
      function () {
        dropdownContent.classList.remove('show');
      }, {
        once: true
      });
  }

  doScroll(menuList: HTMLUListElement) {
    if (menuList.scrollHeight - menuList.scrollTop === menuList.clientHeight) {
      this.doFilterNextPage()
    }
  }
}
