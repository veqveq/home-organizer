import {Component, Input, OnInit} from '@angular/core';
import {DictionaryItem} from "../../models/dictionary-item";
import {ItemService} from "../../services/item.service";
import {TSMap} from "typescript-map";
import {BehaviorSubject, map, Observable, tap} from "rxjs";
import {DictionaryPageComponent} from "../../pages/dictionary-page/dictionary-page.component";
import {
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  ValidationErrors,
  Validators
} from "@angular/forms";
import {DictionaryField} from "../../models/dictionary-field";
import {ValidationService} from "../../services/validation.service";

@Component({
  selector: 'app-dictionary-item-editor',
  templateUrl: './dictionary-item-editor.component.html'
})
export class DictionaryItemEditorComponent implements OnInit {
  @Input() visible$: BehaviorSubject<boolean>
  @Input() page: DictionaryPageComponent
  @Input() isUpdate: boolean = false

  form: FormGroup
  load: boolean
  types = [
    {field: 'Text', type: 'text', placeholder: 'Текст...'},
    {field: 'Double', type: 'number', placeholder: 'Дробное число...'},
    {field: 'Long', type: 'number', placeholder: 'Целое число...'},
    {field: 'Date', type: 'date', placeholder: ''},
    {field: 'Boolean', type: 'checkbox', placeholder: ''}
  ]

  itemId: string

  constructor(private itemService: ItemService,
              private validationService: ValidationService,
              private fb: FormBuilder) {
  }

  ngOnInit(): void {
    this.initForm()
  }

  public initForm(): void {
    let newForm = this.fb.group({
      id: [''],
      dictionaryId: this.page.dictionary.id,
      fieldValues: this.fb.array([])
    })
    const arrayControl = <FormArray>newForm.controls['fieldValues']
    this.page.dictionary.fields.forEach(fld => {
      let newGroup = this.fb.group({});
      let control
      if (fld.type == 'Boolean') {
        control = this.fb.control<boolean>(fld.defaultValue == 'true')
      } else {
        control = this.fb.control('')
      }
      if (fld.required) {
        control.addValidators(Validators.required)
      }
      if (fld.unique) {
        newGroup.addAsyncValidators([this.uniqueValidator.bind(this)])
      }
      newGroup.addControl(fld.id, control)
      arrayControl.push(newGroup)
    })
    this.form = newForm
  }

  public setExistedItem(item: DictionaryItem): void {
    this.itemId = item.id
    let newForm = this.fb.group({
      id: item.id,
      dictionaryId: this.page.dictionary.id,
      fieldValues: this.fb.array([])
    })
    const arrayControl = <FormArray>newForm.controls['fieldValues']
    let itemFields: TSMap<string, any> = new TSMap<string, any>().fromJSON(JSON.parse(JSON.stringify(item.fieldValues)));
    this.page.dictionary.fields.forEach(fld => {
      let newGroup = this.fb.group({});
      if (fld.id) {
        let item
        let control

        if (itemFields.has(fld.id)) {
          item = itemFields.get(fld.id)
        }

        if (fld.type == 'Boolean') {
          control = this.fb.control<boolean>(item == 'true')
          console.log('exist', item, control)
        } else {
          control = this.fb.control(item)
        }
        if (fld.required) {
          control.addValidators(Validators.required)
        }
        if (fld.unique) {
          newGroup.addAsyncValidators([this.uniqueValidator.bind(this)])
        }
        newGroup.addControl(fld.id, control)
        arrayControl.push(newGroup)
      }
    })
    this.form = newForm
  }

  public save() {
    this.load = true
    this.itemService.create(this.page.dictionary.id, this.getItem())
      .subscribe(() => {
        this.itemService.filter(this.page.dictionary.id, this.page.filter, this.page.getSortParam(), this.page.page.size, this.page.page.number)
          .pipe(
            tap((resp) => {
              this.page.page = resp
              this.load = false
            }),
            map((resp) => resp.content)
          )
          .subscribe(items => this.page.items = items)
        this.close()
      })
  }

  public update() {
    this.load = true
    this.itemService.update(this.page.dictionary.id, this.form.controls.id.value, this.getItem())
      .subscribe(() => {
        this.itemService.filter(this.page.dictionary.id, this.page.filter, this.page.getSortParam(), this.page.page.size, this.page.page.number)
          .pipe(
            tap((resp) => this.page.page = resp),
            map((resp) => resp.content)
          )
          .subscribe(items => this.page.items = items)
        this.close()
      })
  }

  public close() {
    this.load = false
    this.visible$.next(false)
  }

  private getItem(): DictionaryItem {
    let item = this.form.value as DictionaryItem
    let fieldsMap: TSMap<string, any> = new TSMap<string, any>()
    let form = this.form.controls.fieldValues as FormArray
    for (let valueElement of form.value) {
      if (Object.values(valueElement)[0] || Object.values(valueElement)[0] == false) {
        fieldsMap.set(Object.keys(valueElement)[0], Object.values(valueElement)[0])
      }
    }
    item.fieldValues = fieldsMap
    return item
  }

  getInputType(type: string) {
    return this.types.filter(val => val.field == type)[0].type
  }

  getPlaceholder(type: string) {
    return this.types.filter(val => val.field == type)[0].placeholder
  }

  setDefaultValue(field: DictionaryField, index: number) {
    if (field.defaultValue) {
      let formControl = this.getFormControl(field, index)
      formControl.setValue(field.defaultValue)
    }
  }

  get fieldValues(): FormArray {
    return this.form.controls['fieldValues'] as FormArray
  }

  getFormControl(field: DictionaryField, index: number): FormControl {
    return (<FormGroup>this.fieldValues.at(index)).controls[field.id ? field.id : ''] as FormControl
  }

  private uniqueValidator({value: field}: FormGroup): Observable<ValidationErrors | null> {
    let id = Object.keys(field)[0]
    let value = Object.values(field)[0]
    return this.validationService.validateUnique(this.page.dictionary.id, this.itemId, id, value)
  }

  onClick(fld: DictionaryField, input: HTMLInputElement, i: number) {
    if (fld.type == 'Boolean') {
      this.getFormControl(fld, i).setValue(input.checked)
    } else {
      if (input.value == '') {
        this.setDefaultValue(fld, i)
      }
    }
  }
}
