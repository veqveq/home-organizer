import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {DictionaryItem} from "../../models/dictionary-item";
import {ItemService} from "../../services/item.service";
import {TSMap} from "typescript-map";
import {BehaviorSubject, concatMap, delay, Observable, tap} from "rxjs";
import {DictionaryPageComponent} from "../../pages/dictionary-page/dictionary-page.component";

@Component({
  selector: 'app-dictionary-item-editor',
  templateUrl: './dictionary-item-editor.component.html'
})
export class DictionaryItemEditorComponent implements OnInit {
  @Input() visible$: BehaviorSubject<boolean>
  @Input() page: DictionaryPageComponent
  @Input() existedItem: DictionaryItem

  item: DictionaryItem = new class implements DictionaryItem {
    fieldValues: TSMap<string, any> = new TSMap();
    dictionaryId: string;
    id: string;
  };

  constructor(private itemService: ItemService) {
  }

  ngOnInit(): void {
    if (this.existedItem != null) {
      console.log(this.existedItem)
      this.item.id = this.existedItem.id
      this.item.dictionaryId = this.existedItem.dictionaryId
      this.item.fieldValues = new TSMap<string, any>().fromJSON(JSON.parse(JSON.stringify(this.existedItem.fieldValues)))
    } else {
      this.item.dictionaryId = this.page.dictionary.id
    }
  }

  save() {
    if (this.page.dictionary.id) {
      const ID = this.page.dictionary.id
      this.itemService.create(ID, this.item)
        .subscribe(() => {
          this.itemService.filter(ID, this.page.filter, this.page.getSortParam())
            .subscribe(items => this.page.items = items)
          this.close()
          this.clean()
        })
    }
  }

  update() {
    if (this.page.dictionary.id) {
      const ID = this.page.dictionary.id
      if (this.existedItem.id) {
        this.itemService.update(ID, this.existedItem.id, this.item)
          .subscribe(() => {
            this.itemService.filter(ID,this.page.filter, this.page.getSortParam())
              .subscribe(items => this.page.items = items)
            this.close()
          })
      }
    }
  }

  close() {
    this.visible$.next(false)
  }

  clean() {
    let col = document.getElementsByClassName('input') as HTMLCollectionOf<HTMLInputElement>
    Array.from(col).forEach(el => el.value = '')
    this.item = new class implements DictionaryItem {
      fieldValues: TSMap<string, any> = new TSMap();
      dictionaryId: string;
      id: string;
    };
  }

  getExistedFieldValue(fieldId: string) {
    let val = ''
    if (this.existedItem) {
      val = JSON.parse(JSON.stringify(this.existedItem.fieldValues))[fieldId]
    }
    return val == undefined ? '' : val
  }

  setDefaultValue(inp: HTMLInputElement, defaultValue: string, fldId: string) {
    if (inp.value == '' && defaultValue != '') {
      inp.value = defaultValue
      this.item.fieldValues.set(fldId, defaultValue)
    }
  }
}
