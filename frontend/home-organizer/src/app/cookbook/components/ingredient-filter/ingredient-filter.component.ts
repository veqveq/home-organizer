import {Component, ElementRef, EventEmitter, HostListener, Input, OnInit, Output, ViewChild} from '@angular/core';
import {RecipeCompFilter} from "../../models/recipe-comp-filter";
import {RecipeComp} from "../../models/recipe-comp";
import {RecipeCompService} from "../../services/recipe-comp.service";
import {map, tap} from "rxjs";
import {GroupIngredientName} from "../../models/group-ingredient-name";

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

  pickedValues: Map<GroupIngredientName, RecipeComp[]> = new Map<GroupIngredientName, RecipeComp[]>()

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
    private service: RecipeCompService
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

  pickIngredient(ingredient: RecipeComp, doEmit: boolean = true) {
    ingredient.hidden = true
    let ingredientGroup = ingredient.group ? ingredient.group : new GroupIngredientName(ingredient.id, ingredient.name, ingredient)
    let pickedGroup: GroupIngredientName = Array.from(this.pickedValues.keys()).find(picked => picked.id === ingredientGroup.id)

    if (pickedGroup) {
      let pickedIngredients: RecipeComp[] = this.pickedValues.get(pickedGroup)
      pickedIngredients.push(ingredient)
      this.pickedValues.set(pickedGroup, pickedIngredients)
      if (this.getNotPickedFromGroupCount(pickedGroup) == 0) {
        pickedGroup.hidden = true
      }
    } else {
      this.pickedValues.set(ingredientGroup, [ingredient])
      if (this.getNotPickedFromGroupCount(ingredientGroup) == 0) {
        ingredientGroup.hidden = true
      }
    }
    if (doEmit) {
      this.filterComponents.emit(this.pickedValues)
    }
  }

  pickGroup(group: GroupIngredientName) {
    group.hidden = true
    if (this.getNotPickedFromGroupCount(group) > 0) {
      group.childIngredientNames
        .filter(ingredient => !ingredient.hidden)
        .forEach(ingredient => this.pickIngredient(ingredient, false))
    } else {
      this.pickedValues.set(group, [new RecipeComp(group.id, group.name, group)])
    }
    this.filterComponents.emit(this.pickedValues)
    if (this.groups.length == 0) {
      this.closeMenu()
    }
  }

  getNotPickedFromGroupCount(group: GroupIngredientName) {
    return group.childIngredientNames.filter(ingredient => !ingredient.hidden).length
  }

  getTotalPickedValuesCount() {
    let counter = 0
    this.pickedValues.forEach((value) => counter += value.length)
    return counter
  }

  returnIngredient(ingredient: RecipeComp) {
    ingredient.hidden = false
    let ingredientGroupId = ingredient.group ? ingredient.group.id : ingredient.id
    let pickedGroup: GroupIngredientName = Array.from(this.pickedValues.keys()).find(picked => picked.id === ingredientGroupId)
    if (pickedGroup) {
      let valuesList = this.pickedValues.get(pickedGroup).filter(value => value !== ingredient)
      pickedGroup.hidden = false
      if (valuesList.length == 0) {
        this.pickedValues.delete(pickedGroup)
      } else {
        this.pickedValues.set(pickedGroup, valuesList)
      }
    }
  }

  returnGroup(group: GroupIngredientName){
    let pickedGroup: GroupIngredientName = Array.from(this.pickedValues.keys()).find(picked => picked.id === group.id)
    if(pickedGroup){
      pickedGroup.hidden = false
      pickedGroup.childIngredientNames.forEach(ingredient=>ingredient.hidden=false)
      this.pickedValues.delete(pickedGroup)
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
      }
    )
  }

  doFilter() {
    this.service.doFilter<GroupIngredientName>(this.filter, this.endpoint, 0)
      .pipe(
        tap(resp => {
          if (!resp.last) {
            this.page = resp.number + 1
            this.totalPages = resp.totalPages
          }
        }),
        map((resp) => resp.content as GroupIngredientName[])
      )
      .subscribe(items => this.groups = this.handleRecivedItems(items))
  }

  doFilterNextPage() {
    if (this.page < this.totalPages - 1) {
      this.service.doFilter<GroupIngredientName>(this.filter, this.endpoint, this.page)
        .pipe(
          tap(resp => {
            if (!resp.last) {
              this.page = resp.number + 1
            }
          }),
          map((resp) => resp.content as GroupIngredientName[])
        )
        .subscribe(items => this.groups.push(...this.handleRecivedItems(items)))
    }
  }

  handleRecivedItems(nameGroups: GroupIngredientName[]) {
    nameGroups.forEach(group => {
      if (group.childIngredientNames.length > 0) {
        //Добавление элемента с названием группы
        group.childIngredientNames.splice(0, 0, new RecipeComp(group.id, group.name))
        //Добавление обратной ссылки на группу в элемент
        group.childIngredientNames.forEach(ingredient => ingredient.group = group)
      }

      //Проверка что ингредиенты группы добавлены в фильтра
      let pickedGroup: GroupIngredientName = Array.from(this.pickedValues.keys()).find(picked => picked.id === group.id)
      if (pickedGroup) {
        let pickedIngredients: RecipeComp[] = this.pickedValues.get(pickedGroup)
        if (pickedIngredients) {
          let pickedIngredientIds: string[] = pickedIngredients.map(picked => picked.id)
          //Если ингредиенты добавлены - скрываем их в группе
          group.childIngredientNames.forEach(child => {
            if (pickedIngredientIds.includes(child.id)) {
              child.hidden = true
            }
          })
        }
      }

      //Проверка что группа добавлена в фильтр целиком
      if (group.childIngredientNames.length) {
        //Если в группе есть элементы, то скрываем если все элементы скрыты
        group.hidden = this.getNotPickedFromGroupCount(group) <= 0
      } else {
        //Если нет элементов - то проверяем, существует ли ключ в мапе
        group.hidden = pickedGroup != undefined
      }
    })
    return nameGroups;
  }

  reset() {
    this.filter.name = ''
    this.inputField.nativeElement.value = ''
    this.pickedValues.clear()
    this.doFilter()
    this.filterComponents.emit(this.pickedValues)
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
