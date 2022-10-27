import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {map, Observable, retry, tap} from "rxjs";
import {ValidationErrors} from "@angular/forms";

@Injectable({
  providedIn: 'root'
})
export class ValidationService {
  ROOT_API: string = "http://localhost:8081/ho/api/v1/items"

  constructor(
    private http: HttpClient
  ) {
  }

  validateUnique(dictionaryId: string, itemId: string, fieldId: string, fieldValue: any): Observable<ValidationErrors | null> {
    if (fieldValue == null){
      return new Observable<ValidationErrors>(observer=> {
        observer.next(null)
        observer.complete()
      })
    }
    return this.http.get<boolean>(this.ROOT_API + '/' + dictionaryId + '/is-unique', {
      params: new HttpParams({
        fromObject: {
          itemId: itemId == undefined ? '' : itemId,
          fieldId: fieldId,
          fieldValue: fieldValue
        }
      })
    }).pipe(map((res) => {
      if (res) {
        return null
      } else {
        return {notUnique: 'Значение не уникально'}
      }
    }))
  }
}
