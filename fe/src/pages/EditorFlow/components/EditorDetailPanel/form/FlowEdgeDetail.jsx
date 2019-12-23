import React from 'react';
import { Form, Item, Input } from 'antd';
import { withPropsAPI } from 'gg-editor';

import CommonForm, { inlineFormItemLayout } from './CommonForm';

class FlowEdgeDetail extends CommonForm {
  render() {
    const { form } = this.props;
    const { label = '', valve = 'true' } = this.item.getModel();
    return (
      <Form onSubmit={this.handleSubmit}>
        <Item label="Label" {...inlineFormItemLayout}>
          {form.getFieldDecorator('label', {
            initialValue: label,
          })(<Input onBlur={this.handleSubmit} />)}
        </Item>
        {/* <Item label="Shape" {...inlineFormItemLayout}>
                    {form.getFieldDecorator('shape', {
                        initialValue: shape,
                    })(this.renderEdgeShapeSelect())}
                </Item> */}
        <Item label="valve" {...inlineFormItemLayout}>
          {form.getFieldDecorator('valve', {
            initialValue: valve,
          })(<Input onBlur={this.handleSubmit} />)}
        </Item>
      </Form>
    );
  }
}

export default Form.create()(withPropsAPI(FlowEdgeDetail));
