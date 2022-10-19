import {Injectable} from "@angular/core";
import {HttpClient, HttpErrorResponse, HttpParams} from "@angular/common/http";
import {Dictionary} from "../models/dictionary";
import {catchError, concatMap, map, Observable, retry, tap, throwError} from "rxjs";
import {Page} from "../models/page";
import {ErrorService} from "./error.service";

@Injectable({
  providedIn: 'root'
})
export class DictionaryService {
  ROOT_API: string = "http://localhost:8081/ho/api/v1/dictionaries"

  dictionaries: Dictionary[] = []

  constructor(
    private http: HttpClient,
    private errorService: ErrorService
  ) {
  }

  getAll(): Observable<Dictionary[]> {
    return this.http.get<Page<Dictionary>>(this.ROOT_API, {
      params: new HttpParams({
        fromObject: {
          page: 0,
          size: 10
        }
      })
    }).pipe(
      map(response => response.content),
      retry(2),
      tap(dictionaries => this.dictionaries = dictionaries),
      catchError(this.handleError.bind(this))
    )
  }

  create(dictionary: Dictionary): Observable<Dictionary> {
    return this.http.post<Dictionary>(this.ROOT_API, dictionary)
      .pipe(
        catchError(this.handleError.bind(this))
      )
  }

  update(dictionaryId: string, dictionary: Dictionary): Observable<Dictionary> {
    return this.http.put<Dictionary>(this.ROOT_API + '/' + dictionaryId, dictionary)
      .pipe(
        catchError(this.handleError.bind(this))
      )
  }

  delete(dictionary: Dictionary) {
    this.http.delete(this.ROOT_API + '/' + dictionary.id).pipe(
      concatMap(() => this.getAll()),
      catchError(this.handleError.bind(this))
    ).subscribe()
  }

  getById(id: string): Observable<Dictionary> {
    return this.http.get<Dictionary>(this.ROOT_API + '/' + id)
      .pipe(
        catchError(this.handleError.bind(this))
      )
  }

  private handleError(error: HttpErrorResponse) {
    this.errorService.handle(error.error.message)
    return throwError(() => error.message)
  }
}
