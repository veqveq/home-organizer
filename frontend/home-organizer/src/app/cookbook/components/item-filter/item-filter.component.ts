import {
  Component,
  ElementRef,
  EventEmitter, HostListener,
  Input,
  OnInit,
  Output,
  ViewChild
} from '@angular/core';
import {RecipeCompFilter} from "../../models/recipe-comp-filter";
import {RecipeCompService} from "../../services/recipe-comp.service";
import {map, tap} from "rxjs";
import {RecipeComp} from "../../models/recipe-comp";

@Component({
  selector: 'app-item-filter',
  templateUrl: './item-filter.component.html'
})
export class ItemFilterComponent implements OnInit {
  @ViewChild('inputField') inputField: ElementRef
  @ViewChild('menu') menu: ElementRef
  @ViewChild('menuList') menuList: ElementRef
  @Input() title: string
  @Input() endpoint: string
  @Output() filterComponents = new EventEmitter();

  values: string[] = []
  filter: RecipeCompFilter = new class implements RecipeCompFilter {
    excludes: string[] = [];
    name: string;
  }
  curElementIndex: number = 0
  page: number = 0
  totalPages: number

  showList = false
  filtered: RecipeComp[] = []
  hasChanges: boolean = false

  constructor(
    private eRef: ElementRef,
    private service: RecipeCompService,
  ) {
  }

  @HostListener('document:click', ['$event'])
  clickout(event) {
    if(!this.eRef.nativeElement.contains(event.target)) {
      if (this.showList){
        this.closeMenu()
      }
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
          if (this.curElementIndex == this.filtered.length) {
            this.curElementIndex = this.filtered.length - 1
            this.doFilterNextPage()
          }
        } else if (ev.key == 'ArrowUp') {
          this.curElementIndex--
          menu.scrollTop = menu.scrollTop - 40
          if (this.curElementIndex < 0) {
            this.curElementIndex = 0
          }
        } else if (ev.key == 'Enter') {
          this.saveValue(this.filtered[this.curElementIndex])
        }
      }
    })
  }

  public saveValue(val: RecipeComp) {
    let valIndex = this.filter.excludes.findIndex(value => value == val.id)
    if (valIndex === -1){
      this.filter.excludes.push(val.id)
      this.values.push(val.name)
      this.filter.name = ''
      this.filterComponents.emit(this.filter.excludes)
      this.doFilter()
      this.hasChanges = true
    }else{
      this.delete(valIndex)
    }
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
      .subscribe(items => this.filtered = items)
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
        .subscribe(items => this.filtered.push(...items))
    }
  }

  delete(i: number) {
    this.values.splice(this.filter.excludes.length - i - 1, 1)
    this.filter.excludes.splice(this.filter.excludes.length - i - 1, 1)
    this.filterComponents.emit(this.filter.excludes)
    if (this.values.length == 0){
      this.hasChanges = false
    }
  }

  reset() {
    this.values = []
    this.filter.excludes = []
    this.hasChanges = false
    this.inputField.nativeElement.value = ''
    this.filterComponents.emit(this.filter.excludes)
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


