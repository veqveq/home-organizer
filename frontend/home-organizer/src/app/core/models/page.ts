export interface Page<T> {
  content: T[],
  number: number
  size: number
  totalPages: number
  first: boolean
  last: boolean
  totalElements: number
}
