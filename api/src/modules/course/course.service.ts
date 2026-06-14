import type { CourseRepository } from "./course.repository";

export class CourseService {
  constructor(private readonly courseRepository: CourseRepository) {}

  async list() {
    return this.courseRepository.listActive();
  }
}
