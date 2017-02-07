*{
 *  create a select element, with the correct options id valueProperty is set
 *  arg (required) name attribute of the generated select
 *  size (optional) size attribute of the generated select
 *  values (optional) selected elements
 *  labelProperty (optional) item property used as option's body
 *  valueProperty (optional) item property used as option's value. id is used by default.
 *  #{select 'hotels', items:hotels, valueProperty:'id', labelProperty:'name' values:selectedHotels} #{/select}
}*
%{
    ( _arg ) &&  ( _name = _arg);

    if(! _name) {
        throw new play.exceptions.TagInternalException("name attribute cannot be empty for select-multiple tag");
    }

    if(!_valueProperty)
        _valueProperty = 'id';

    play.templates.TagContext.current().data.put("selected", _values);
    play.templates.TagContext.current().data.put("valueProperty", _valueProperty);

    serializedAttrs  = play.templates.FastTags.serialize(_attrs, "size", "name", "items", "labelProperty", "values", "valueProperty")
}%

<select name="${_name}" size="${_size?:1}" ${serializedAttrs} multiple="multiple">
    #{doBody /}
    #{list items:_items, as:'i'}
        #{option _valueProperty && i.hasProperty(_valueProperty) ? i[_valueProperty] : i}&{_labelProperty && i.hasProperty(_labelProperty) ? play.utils.HTML.htmlEscape(i[_labelProperty]) : i}#{/option}
    #{/list}
</select>