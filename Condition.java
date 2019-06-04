package db61b;

import java.util.List;

import static db61b.Utils.*;

/** Represents a single 'where' condition in a 'select' command.
 * @author Alex Freeman */
class Condition {

    /** A Condition representing COL1 RELATION COL2, where COL1 and COL2
     *  are column designators. and RELATION is one of the
     *  strings "<", ">", "<=", ">=", "=", or "!=". */
    Condition(Column col1, String relation, Column col2) {
        _result = false;
        switch (relation) {
        case ">":
            _result = true;
            /* fall through */
        case "<=":
            _target = 1;
            break;
        case "<":
            _result = true;
            /* fall through */
        case ">=":
            _target = -1;
            break;
        case "=":
            _result = true;
            /* fall through */
        case "!=":
            _target = 0;
            break;
        default:
            throw error("Unknown comparator: '%s'", relation);
        }
        _col1 = col1;
        _col2 = col2;
    }

    /** A Condition representing COL1 RELATION 'VAL2', where COL1 is
     *  a column designator, VAL2 is a literal value (without the
     *  quotes), and RELATION is one of the strings "<", ">", "<=",
     *  ">=", "=", or "!=".
     */
    Condition(Column col1, String relation, String val2) {
        this(col1, relation, (Column) null);
        _val2 = val2;
    }

    /** Assuming that ROWS are rows from the respective tables from which
     *  my columns are selected, returns the result of performing the test I
     *  denote. */
    boolean test(Row... rows) {
        String second = (_col2 == null) ? _val2 : _col2.getFrom(rows);
        int diff = _col1.getFrom(rows).compareTo(second);
        if (_target == 0) {
            return (diff != 0) ^ _result;
        }
        return !(diff / _target > 0) ^ _result;
    }

    /** Return true iff ROWS satisfies all CONDITIONS. */
    static boolean test(List<Condition> conditions, Row... rows) {
        if (conditions == null) {
            return true;
        }
        for (Condition cond : conditions) {
            if (!cond.test(rows)) {
                return false;
            }
        }
        return true;
    }

    /** The operands of this condition.  _col2 is null if the second operand
     *  is a literal. */
    private Column _col1, _col2;
    /** Second operand, if literal (otherwise null). */
    private String _val2;
    /** The result we're looking for when we compare the two items. */
    private int _target;
    /** What to return when _col1.compareTo(_col2) == _something. */
    private boolean _result;
}
